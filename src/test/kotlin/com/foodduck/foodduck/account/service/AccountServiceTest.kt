package com.foodduck.foodduck.account.service

import com.foodduck.foodduck.account.dto.*
import com.foodduck.foodduck.account.model.Account
import com.foodduck.foodduck.account.repository.AccountRepository
import com.foodduck.foodduck.account.repository.ReasonRepository
import com.foodduck.foodduck.base.config.security.jwt.JwtProvider
import com.foodduck.foodduck.base.config.security.token.TokenDto
import com.foodduck.foodduck.base.error.CustomException
import com.foodduck.foodduck.base.error.ErrorCode
import com.foodduck.foodduck.base.message.PrefixType
import com.foodduck.foodduck.base.util.FoodDuckUtil
import io.mockk.*
import io.mockk.impl.annotations.MockK
import io.mockk.impl.annotations.RelaxedMockK
import org.assertj.core.api.Assertions.*
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.repository.findByIdOrNull
import org.springframework.mail.SimpleMailMessage
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.test.util.ReflectionTestUtils
import java.time.Duration
import java.util.*
import javax.servlet.http.HttpServletRequest

internal class AccountServiceTest {

    private lateinit var accountService: AccountService

    @MockK
    private lateinit var accountRepository: AccountRepository

    @MockK
    private lateinit var jwtProvider: JwtProvider

    @MockK
    private lateinit var passwordEncoder: PasswordEncoder

    @MockK
    private lateinit var redisTemplate: RedisTemplate<String, String>

    @RelaxedMockK
    private lateinit var javaMailSender: JavaMailSender

    @MockK
    private lateinit var reasonRepository: ReasonRepository

    @BeforeEach
    fun setUp() {
        MockKAnnotations.init(this)
        mockkStatic(FoodDuckUtil::class)
        mockkObject(FoodDuckUtil)
        every { FoodDuckUtil.authenticationNumber() }.returns("12345")
        accountService = AccountService(
            accountRepository,
            jwtProvider,
            passwordEncoder,
            redisTemplate,
            javaMailSender,
            reasonRepository
        )
    }

    @AfterEach
    fun tearDown() {
        clearStaticMockk(FoodDuckUtil::class)
    }

    @Test
    fun `회원가입 성공`() {

        val email = "foodduck@example.com"
        val password = "Test12#$"
        val encodePassword = "\$2a\$10\$Y2C2wVyIh5inOWStOe6sNOv4ggk50vOHsP6ZPDwW07YBGpW0i5WHO"
        val nickname = "foodduck"
        val request =
            AccountSignUpRequest(email = email, nickname = nickname, password = password, checkPassword = "Test12#$")
        val account = Account(email = email, password = encodePassword, nickname = nickname)
        val token = TokenDto("accessToken", "refreshToken")

        every { accountRepository.existsByEmail(email) } returns false
        every { accountRepository.existsByNickname(nickname) } returns false
        every { passwordEncoder.encode(password) } returns encodePassword
        every { accountRepository.save(any()) } returns account
        every { jwtProvider.createAllToken(email, Collections.singletonList("ROLE_USER")) } returns token

        val result = accountService.signUp(request)
        assertThat(result).isEqualTo(token)
    }

    @Test
    fun `회원가입 이메일 형식 틀림`() {
        val email = "foodduck@example"
        val password = "Test12#$"
        val nickname = "foodduck"
        val request =
            AccountSignUpRequest(email = email, nickname = nickname, password = password, checkPassword = "Test12#$")

        assertThrows(CustomException::class.java) {
            accountService.signUp(request)
        }
    }

    @Test
    fun `회원가입 비밀번호 형식 틀림`() {
        val email = "foodduck@example.com"
        val password = "Test12"
        val checkPassword = "Test12"
        val nickname = "foodduck"
        val request =
            AccountSignUpRequest(email = email, nickname = nickname, password = password, checkPassword = checkPassword)

        every { accountRepository.existsByEmail(email) } returns false

        assertThrows(CustomException::class.java) {
            accountService.signUp(request)
        }
    }

    @Test
    fun `회원가입 2차 비밀번호 형식틀림`() {
        val email = "foodduck@example.com"
        val password = "Test12#$"
        val checkPassword = "Test12"
        val nickname = "foodduck"
        val request =
            AccountSignUpRequest(email = email, nickname = nickname, password = password, checkPassword = checkPassword)

        every { accountRepository.existsByEmail(email) } returns false

        assertThrows(CustomException::class.java) {
            accountService.signUp(request)
        }
    }

    @Test
    fun `회원가입 닉네임 이미 존재함`() {
        val email = "foodduck@example.com"
        val password = "Test12#$"
        val checkPassword = "Test12#$"
        val nickname = "foodduck"
        val request =
            AccountSignUpRequest(email = email, nickname = nickname, password = password, checkPassword = checkPassword)

        every { accountRepository.existsByEmail(email) } returns false
        every { accountRepository.existsByNickname(nickname) } returns true

        assertThrows(CustomException::class.java) {
            accountService.signUp(request)
        }
    }

    @Test
    fun `회원가입 비밀번호 2차 비밀번호 불일치`() {
        val email = "foodduck@example.com"
        val password = "Test12#$"
        val checkPassword = "Test12#4"
        val nickname = "foodduck"
        val request =
            AccountSignUpRequest(email = email, nickname = nickname, password = password, checkPassword = checkPassword)

        every { accountRepository.existsByEmail(email) } returns false
        every { accountRepository.existsByNickname(nickname) } returns true

        assertThrows(CustomException::class.java) {
            accountService.signUp(request)
        }
    }

    @Test
    fun `닉네임 확인`() {
        val nickname = "foodduck"

        every { accountRepository.existsByNickname(nickname) } returns false

        assertDoesNotThrow {
            accountService.checkNickname(nickname)
        }
    }

    @Test
    fun `닉네임 이미 존재했을 때`() {
        val nickname = "foodduck"

        every { accountRepository.existsByNickname(nickname) } returns true

        assertThrows(CustomException::class.java) {
            accountService.checkNickname(nickname)
        }
    }

    @Test
    fun `로그인 시도`() {
        val email = "foodduck@example.com"
        val password = "Test12#$"
        val nickname = "foodduck"
        val encodePassword = "\$2a\$10\$Y2C2wVyIh5inOWStOe6sNOv4ggk50vOHsP6ZPDwW07YBGpW0i5WHO"
        val request = AccountLoginRequest(email = email, password = password)
        val account = Account(email = email, password = encodePassword, nickname = nickname)
        val token = TokenDto("accessToken", "refreshToken")


        every { accountRepository.findByEmail(email) } returns account
        every { passwordEncoder.encode(password) } returns encodePassword
        every { passwordEncoder.matches(password, encodePassword) } returns true
        every { jwtProvider.createAllToken(email, Collections.singletonList("ROLE_USER")) } returns token
        every { jwtProvider.saveRefreshToken(email, "refreshToken") } returnsArgument 0

        val result = accountService.login(request)
        assertThat(result).isEqualTo(token)
    }

    @Test
    fun `로그인 시도 이미 이메일이 존재한다면`() {
        val email = "foodduck@example.com"
        val password = "Test12#$"
        val request = AccountLoginRequest(email = email, password = password)

        every { accountRepository.findByEmail(email) } throws CustomException(ErrorCode.USER_NOT_FOUND_ERROR)
        assertThrows(CustomException::class.java) {
            accountService.login(request)
        }
    }

    @Test
    fun `로그인 시도 비밀번호가 다를때`() {
        val email = "foodduck@example.com"
        val password = "Test12#$"
        val nickname = "foodduck"
        val encodePassword = "\$2a\$10\$Y2C2wVyIh5inOWStOe6sNOv4ggk50vOHsP6ZPDwW07YBGpW0i5WHO"
        val request = AccountLoginRequest(email = email, password = password)
        val account = Account(email = email, password = encodePassword, nickname = nickname)


        every { accountRepository.findByEmail(email) } returns account
        every { passwordEncoder.encode(password) } returns encodePassword
        every { passwordEncoder.matches(password, encodePassword) } returns false

        assertThrows(CustomException::class.java) {
            accountService.login(request)
        }
    }

    @Test
    fun `accessToken 이 만료되었을 때 refreshToken 값을 주면 다시 accessToken 과 refreshToken 을 준다`() {
        val email = "foodduck@example.com"
        val refreshToken = "refreshToken===="
        val tokenDto = TokenDto("newAccessToken", "newRefreshToken")
        every { jwtProvider.checkRefreshToken(email, refreshToken) } returnsArgument 0
        every { jwtProvider.reIssueAllToken(email, Collections.singletonList("ROLE_USER")) } returns tokenDto

        val result = accountService.reIssueToken(email, refreshToken)
        assertThat(result).isEqualTo(tokenDto)
    }

    @Test
    fun `본인 인증 비밀번호 보내기`() {
        ReflectionTestUtils.setField(accountService, "sendFrom", "foodduck@duck.co.kr")

        val email = "foodduck@example.com"
        val key = PrefixType.TEMP_PASSWORD.prefix + email
        val number = "12345"

        val simpleMailMessage = SimpleMailMessage()
        simpleMailMessage.setTo()
        simpleMailMessage.setFrom(email)
        simpleMailMessage.setSubject("Food Duck 인증번호")
        simpleMailMessage.setText(number)

        every { accountRepository.existsByEmail(email) }.returns(true)
        every { javaMailSender.send(simpleMailMessage) } returnsArgument 0
        every { redisTemplate.opsForValue().set(key, number) }.returnsArgument(0)
        every { redisTemplate.expire(key, Duration.ofMinutes(FoodDuckUtil.AUTHENTICATE_DURATION_MINUTE)) }.returns(true)

        assertDoesNotThrow {
            accountService.sendTempAuthenticateNumber(email)
        }
    }

    @Test
    fun `로그아웃`() {
        val email = "foodduck@example.com"
        val encodePassword = "\$2a\$10\$Y2C2wVyIh5inOWStOe6sNOv4ggk50vOHsP6ZPDwW07YBGpW0i5WHO"
        val nickname = "foodduck"
        val account = Account(email = email, password = encodePassword, nickname = nickname)
        val request = mockk<HttpServletRequest>()

        every { jwtProvider.logout(request, email) }.returnsArgument(0)

        assertDoesNotThrow {
            accountService.logout(account, request)
        }
    }

    @Test
    fun `인증번호 비교하기`() {
        val email = "foodduck@example.com"
        val number = "12345"
        val key = PrefixType.TEMP_PASSWORD.prefix + email
        every { redisTemplate.opsForValue().get(key) }.returns(number)
        every { redisTemplate.delete(key) }.returns(true)

        assertDoesNotThrow {
            accountService.compareAuthenticateNumber(email, number)
        }
    }

    @Test
    fun `인증번호 틀렸을 때`() {
        val email = "foodduck@example.com"
        val number = "12345"
        val key = PrefixType.TEMP_PASSWORD.prefix + email
        every { redisTemplate.opsForValue().get(key) }.returns("54321")

        assertThrows(CustomException::class.java) {
            accountService.compareAuthenticateNumber(email, number)
        }
    }

    @Test
    fun `비밀번호 변경`() {
        val email = "fodduck@example.com"
        val password = "Test12#$"
        val checkPassword = "Test12#$"
        val encodePassword = "\$2a\$10\$Y2C2wVyIh5inOWStOe6sNOv4ggk50vOHsP6ZPDwW07YBGpW0i5WHO"
        val nickname = "foodduck"
        val account = Account(email = email, password = encodePassword, nickname = nickname)
        val request = AccountChangePasswordRequest(password = password, checkPassword = checkPassword)

        every { accountRepository.findByEmail(email) }.returns(account)
        every { passwordEncoder.encode(password) }.returns(encodePassword)

        assertDoesNotThrow {
            accountService.changePassword(email, request)
        }
    }

    @Test
    fun `비밀번호 변경 포맷문제1`() {
        val email = "fodduck@example.com"
        val password = "Test12"
        val checkPassword = "Test12#$"
        val request = AccountChangePasswordRequest(password = password, checkPassword = checkPassword)
        assertThrows(CustomException::class.java) {
            accountService.changePassword(email, request)
        }
    }

    @Test
    fun `비밀번호 변경 포맷문제2`() {
        val email = "fodduck@example.com"
        val password = "Test12#$"
        val checkPassword = "Test12"
        val request = AccountChangePasswordRequest(password = password, checkPassword = checkPassword)
        assertThrows(CustomException::class.java) {
            accountService.changePassword(email, request)
        }
    }

    @Test
    fun `비밀번호 변경 1차 2차 비밀번호 불일치`() {
        val email = "fodduck@example.com"
        val password = "Test12#$"
        val checkPassword = "Test12#1"
        val request = AccountChangePasswordRequest(password = password, checkPassword = checkPassword)
        assertThrows(CustomException::class.java) {
            accountService.changePassword(email, request)
        }
    }

    @Test
    fun `유저가 존재하지 않는다면`() {
        val email = "fodduck@example.com"
        val password = "Test12#$"
        val checkPassword = "Test12#1"
        val request = AccountChangePasswordRequest(password = password, checkPassword = checkPassword)

        every { accountRepository.findByEmail(email) }.throws(CustomException(ErrorCode.USER_NOT_FOUND_ERROR))

        assertThrows(CustomException::class.java) {
            accountService.changePassword(email, request)
        }
    }

    @Test
    fun `회원 탈퇴`() {
        val email = "fodduck@example.com"
        val encodePassword = "\$2a\$10\$Y2C2wVyIh5inOWStOe6sNOv4ggk50vOHsP6ZPDwW07YBGpW0i5WHO"
        val nickname = "foodduck"
        val account = Account(email = email, password = encodePassword, nickname = nickname)
        val request = SignOutRequest(reason = "simple")
        every { reasonRepository.save(any()) }.returnsArgument(0)
        assertDoesNotThrow {
            accountService.signOut(account, request)
        }
    }

    @Test
    fun `로그인 후 비밀번호 변경`() {
        val email = "fodduck@example.com"
        val password = "Test12#$"
        val encodePassword = "\$2a\$10\$Y2C2wVyIh5inOWStOe6sNOv4ggk50vOHsP6ZPDwW07YBGpW0i5WHO"
        val nickname = "foodduck"
        val account = Account(id = 1L, email = email, password = encodePassword, nickname = nickname)
        val changePassword = "myPass12#$"
        val changePassword2 = "myPass12#$"
        val request = LoginAccountChangePasswordRequest(password, changePassword, changePassword2)

        every { passwordEncoder.matches(password, encodePassword) }.returns(true)
        every { accountRepository.findByIdOrNull(account.id) }.returns(account)
        every { passwordEncoder.encode(changePassword) }.returnsArgument(0)

        assertDoesNotThrow {
            accountService.loginChangePassword(account, request)
        }
    }


}