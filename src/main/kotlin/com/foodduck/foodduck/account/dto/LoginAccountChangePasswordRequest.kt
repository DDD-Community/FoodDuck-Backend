package com.foodduck.foodduck.account.dto

import com.foodduck.foodduck.base.error.CustomException
import com.foodduck.foodduck.base.error.ErrorCode

data class LoginAccountChangePasswordRequest(
    val beforePassword: String,
    val password: String,
    val checkPassword: String
){
    fun validateEqualPassword() {
        if (password != checkPassword) {
            throw CustomException(ErrorCode.NOT_EQUAL_PASSWORD_ERROR)
        }
    }
}

