package com.foodduck.foodduck.account.service

import com.foodduck.foodduck.account.dto.AccountChangePasswordRequest
import com.foodduck.foodduck.account.dto.AccountLoginRequest
import com.foodduck.foodduck.account.dto.AccountSignUpRequest
import com.foodduck.foodduck.account.model.Account
import com.foodduck.foodduck.account.repository.AccountRepository
import com.foodduck.foodduck.base.config.security.jwt.JwtProvider
import com.foodduck.foodduck.base.config.security.token.TokenDto
import com.foodduck.foodduck.base.error.CustomException
import com.foodduck.foodduck.base.error.ErrorCode
import com.foodduck.foodduck.base.message.PrefixType
import com.foodduck.foodduck.base.util.RegexUtil
import com.foodduck.foodduck.base.util.FoodDuckUtil
import org.springframework.beans.factory.annotation.Value
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.redis.core.ValueOperations
import org.springframework.mail.SimpleMailMessage
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.Duration
import java.util.*
import javax.servlet.http.HttpServletRequest

@Service
@Transactional
class AccountService(
    private val accountRepository: AccountRepository,
    private val jwtProvider: JwtProvider,
    private val passwordEncoder: PasswordEncoder,
    private val redisTemplate: RedisTemplate<String, String>,
    private val javaMailSender: JavaMailSender
) {

    @Value("\${spring.mail.username}")
    private lateinit var sendFrom: String

    @Transactional(readOnly = true)
    fun checkNickname(nickname:String) {
        isExistsNickname(nickname)
    }

    private fun isExistsNickname(nickname: String) {
        val isExists = accountRepository.existsByNickname(nickname)
        if (isExists) {
            throw CustomException(ErrorCode.ALREADY_EXISTS_NICKNAME_ERROR)
        }
    }

    fun signUp(request: AccountSignUpRequest): TokenDto {
        validateSignUp(request)
        val account: Account = request.toAccount(passwordEncoder)
        accountRepository.save(account)
        return makeToken(request.email)
    }

    fun signOut(account: Account) {
        account.delete = true
    }

    private fun validateSignUp(request: AccountSignUpRequest) {
        validateEmail(request.email)
        checkUniqueEmail(request.email)
        validatePassword(request.password)
        validatePassword(request.checkPassword)
        isExistsNickname(request.nickname)
        request.validateEqualPassword()
    }

    fun login(request: AccountLoginRequest): TokenDto {
        validateLogin(request)
        val account = accountRepository.findByEmail(request.email) ?: throw CustomException(ErrorCode.USER_NOT_FOUND_ERROR)
        checkPassword(account, request)
        val tokenDto: TokenDto = makeToken(request.email)
        jwtProvider.saveRefreshToken(request.email, tokenDto.refreshToken)
        return tokenDto
    }

    private fun validateLogin(request: AccountLoginRequest) {
        validateEmail(request.email)
        validatePassword(request.password)
    }

    private fun checkPassword(account: Account, request: AccountLoginRequest) {
        val password: String = account.password
        val isMatch: Boolean = passwordEncoder.matches(request.password, password)
        if (!isMatch) {
            throw CustomException(ErrorCode.WRONG_PASSWORD_ERROR)
        }
    }

    private fun makeToken(email: String): TokenDto {
        return jwtProvider.createAllToken(email, Collections.singletonList("ROLE_USER"))
    }

    @Transactional(readOnly = true)
    fun logout(account: Account, request: HttpServletRequest) {
        jwtProvider.logout(request, account.email)
    }

    fun reIssueToken(email: String, refreshToken: String): TokenDto {
        jwtProvider.checkRefreshToken(email, refreshToken)
        return jwtProvider.reIssueAllToken(email, Collections.singletonList("ROLE_USER"))
    }

    fun sendTempAuthenticateNumber(email: String) {
        validateEmail(email)
        existsBy(email)
        val key: String = PrefixType.TEMP_PASSWORD.prefix + email
        val valueOperation: ValueOperations<String, String> = redisTemplate.opsForValue()
        val number = FoodDuckUtil.authenticationNumber()
        valueOperation.set(key, number)
        redisTemplate.expire(key, Duration.ofMinutes(FoodDuckUtil.AUTHENTICATE_DURATION_MINUTE))
        sendEmail(email, "Food Duck 인증번호", number)
    }

    private fun existsBy(email: String) {
        val isExists = accountRepository.existsByEmail(email)
        if (!isExists) {
            throw CustomException(ErrorCode.USER_NOT_FOUND_ERROR)
        }
    }

    private fun sendEmail(email: String, subject:String, number: String) {
        val simpleMailMessage = SimpleMailMessage()
        simpleMailMessage.setTo(email)
        simpleMailMessage.setFrom(sendFrom)
        simpleMailMessage.setSubject(subject)
        simpleMailMessage.setText(number)
        javaMailSender.send(simpleMailMessage)

    }

    fun compareAuthenticateNumber(email: String, number:String) {
        validateEmail(email)
        val key: String = PrefixType.TEMP_PASSWORD.prefix + email
        val valueOperation: ValueOperations<String, String> = redisTemplate.opsForValue()
        val value = valueOperation.get(key)
        if (value != number) {
            throw CustomException(ErrorCode.NOT_EQUAL_AUTHENTICATE_NUMBER_ERROR)
        }
        redisTemplate.delete(key)
    }

    fun changePassword(email:String, request: AccountChangePasswordRequest) {
        validateChangePassword(request)
        val account: Account = accountRepository.findByEmail(email) ?: throw CustomException(ErrorCode.USER_NOT_FOUND_ERROR)
        account.changePassword(passwordEncoder.encode(request.password))
    }

    private fun validateChangePassword(request: AccountChangePasswordRequest) {
        validatePassword(request.password)
        validatePassword(request.checkPassword)
        request.validateEqualPassword()
    }

    private fun validateEmail(email: String) {
        if (!RegexUtil.validEmail(email)) {
            throw CustomException(ErrorCode.EMAIL_FORMAT_ERROR)
        }
    }

    private fun validatePassword(password: String) {
        if (!RegexUtil.validPassword(password)) {
            throw CustomException(ErrorCode.PASSWORD_FORMAT_ERROR)
        }
    }

    private fun checkUniqueEmail(email: String) {
        val isExists = accountRepository.existsByEmail(email)
        if (isExists) {
            throw CustomException(ErrorCode.ALREADY_EXISTS_EMAIL_ERROR)
        }
    }
}
