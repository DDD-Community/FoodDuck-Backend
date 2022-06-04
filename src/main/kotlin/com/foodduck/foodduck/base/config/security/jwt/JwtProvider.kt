package com.foodduck.foodduck.base.config.security.jwt

import com.foodduck.foodduck.account.service.CustomUserDetailService
import com.foodduck.foodduck.base.config.security.token.TokenDto
import com.foodduck.foodduck.base.error.CustomException
import com.foodduck.foodduck.base.error.ErrorCode
import com.foodduck.foodduck.base.error.ErrorResponse
import io.jsonwebtoken.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.redis.core.ValueOperations
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Component
import java.time.Duration
import java.util.*
import javax.annotation.PostConstruct
import javax.servlet.http.HttpServletRequest
import kotlin.RuntimeException

@Component
class JwtProvider(
    @Value("\${spring.jwt.key}") private var secretKey: String,
    @Value("\${spring.jwt.accessTokenExpiration}") private val accessTokenExpiration: Long,
    @Value("\${spring.jwt.refreshTokenExpiration}") private val refreshTokenExpiration: Long,
    @Autowired private val redisTemplate: RedisTemplate<String, String>,
    @Autowired private val customUserDetailService: CustomUserDetailService
    ) {

    @PostConstruct
    protected fun init() {
        secretKey = Base64.getEncoder().encodeToString(secretKey.encodeToByteArray())
    }

    fun reIssueAllToken(email: String, roles: List<String>):TokenDto {
        val tokenDto: TokenDto = this.createAllToken(email, roles)
        changeRefreshToken(email, tokenDto.refreshToken)
        return tokenDto
    }

    private fun changeRefreshToken(email: String, refreshToken: String) {
        redisTemplate.delete(email)
        saveRefreshToken(email, refreshToken)
    }

    fun createAllToken(email: String, roles: List<String>):TokenDto {
        val accessToken: String = createAccessToken(email, roles)
        val refreshToken: String = createRefreshToken(email, roles)
        return TokenDto(accessToken, refreshToken)
    }

    private fun createAccessToken(email: String, roles: List<String>):String {
        val token = createBasicToken(email, roles, accessTokenExpiration)
        redisTemplate.expire(token, Duration.ofMillis(accessTokenExpiration))
        return token
    }

    private fun createRefreshToken(email: String, roles: List<String>):String {
        val token = createBasicToken(email, roles, refreshTokenExpiration)
        redisTemplate.expire(token, Duration.ofMillis(refreshTokenExpiration))
        return token
    }

    fun createBasicToken(email: String, roles: List<String>, tokenValid: Long):String {
        val claims: Claims = Jwts.claims().setSubject(email)
        claims["roles"] = roles

        val date = Date()
        return Jwts.builder()
            .setClaims(claims)
            .setIssuedAt(date)
            .setExpiration(Date(date.time+tokenValid))
            .signWith(SignatureAlgorithm.HS256, secretKey)
            .compact()
    }

    fun getAuthentication(token: String): Authentication {
        val userDetails: UserDetails = customUserDetailService.loadUserByUsername(getSubject(token))
        return UsernamePasswordAuthenticationToken(userDetails, "",userDetails.authorities)
    }

    private fun parseToken(token: String): Jws<Claims>? {
        return Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token)
    }

    private fun getExpiration(token: String):Date {
        return parseToken(token)!!.body.expiration
    }

    private fun getSubject(token: String): String {
        return parseToken(token)!!.body.subject
    }

    fun getAccessTokenFromHeader(request: HttpServletRequest): String? {
        val token: String? = request.getHeader("Authorization")
        if (token != null) {
            return token.substring(7)
        }
        return null
    }

    fun validateToken(request: HttpServletRequest, token: String): Boolean {
        val operations: ValueOperations<String, String> = redisTemplate.opsForValue()
        try {
            val claims: Jws<Claims> = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token)
            val isBlackList: Optional<String> = Optional.ofNullable(operations.get(token))
            if (isBlackList.isPresent) {
                throw CustomException(ErrorCode.JWT_BLACK_LIST_ERROR)
            }
            return !claims.body.expiration.before(Date())
        } catch (e: Exception) {
            when (e) {
                is ExpiredJwtException, is UsernameNotFoundException -> {
                    request.setAttribute("exception", ErrorResponse.of(ErrorCode.USER_NOT_FOUND_ERROR))
                    return false
                }
                is MalformedJwtException -> {
                    request.setAttribute("exception", ErrorResponse.of(ErrorCode.JWT_WRONG_FORMAT_ERROR))
                    return false
                }
                is IllegalArgumentException -> {
                    request.setAttribute("exception", ErrorResponse.of(ErrorCode.TOKEN_PARSER_ERROR))
                    return false
                }
                else -> {
                    throw e
                }
            }
        }
    }

    fun saveRefreshToken(email: String, refreshToken: String) {
        val values: ValueOperations<String, String> = redisTemplate.opsForValue()
        val expiration: Long = getExpiration(refreshToken).time - System.currentTimeMillis()
        values.set(email, refreshToken, Duration.ofMillis(expiration))
    }

    fun checkRefreshToken(email: String, refreshToken: String) {
        val values: ValueOperations<String, String> = redisTemplate.opsForValue()
        val savedRefreshToken: String? = values.get(email)
        if (refreshToken != savedRefreshToken) {
            throw RuntimeException()
        }
    }

    fun logout(request: HttpServletRequest, email: String) {
        setBlackListAccessToken(request)
        redisTemplate.delete(email)
    }

    fun setBlackListAccessToken(request: HttpServletRequest) {
        val accessToken: String? = getAccessTokenFromHeader(request)
        val values: ValueOperations<String, String> = redisTemplate.opsForValue()
        val expiration: Long = accessToken?.let { getExpiration(it).time }?.minus(System.currentTimeMillis()) ?: 0
        if (expiration > 0 && accessToken != null) {
                values.set(accessToken, "BlackList", Duration.ofMillis(expiration))
        }
    }
}