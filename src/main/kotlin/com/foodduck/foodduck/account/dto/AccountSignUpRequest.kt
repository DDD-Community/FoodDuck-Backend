package com.foodduck.foodduck.account.dto

import com.foodduck.foodduck.account.model.Account
import io.swagger.annotations.ApiModelProperty
import org.springframework.security.crypto.password.PasswordEncoder

data class AccountSignUpRequest(
    @ApiModelProperty(value = "이메일", example = "foodduck@example.com")
    val email: String,
    @ApiModelProperty(value = "닉네임", example = "foodduck")
    val nickname: String,
    @ApiModelProperty(value = "1차 비밀번호", example = "Test12#$", notes = "8자 이상 20자 이하, 특수기호, 대문자 최소 한 개 이상 존재")
    val password: String,
    @ApiModelProperty(value = "2차 비밀번호", example = "Test12#$", notes = "8자 이상 20자 이하, 특수기호, 대문자 최소 한 개 이상 존재")
    val checkPassword: String
) {
    fun validateEqualPassword() {
        if (password != checkPassword) {
            throw RuntimeException()
        }
    }
    fun toAccount(passwordEncoder: PasswordEncoder):Account {
        return Account(nickname = nickname, email = email, password = passwordEncoder.encode(password))
    }
}

