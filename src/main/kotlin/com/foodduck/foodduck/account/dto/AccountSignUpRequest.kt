package com.foodduck.foodduck.account.dto

import com.foodduck.foodduck.account.model.Account
import org.springframework.security.crypto.password.PasswordEncoder

data class AccountSignUpRequest(
    val email: String,
    val nickname: String,
    val password: String,
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

