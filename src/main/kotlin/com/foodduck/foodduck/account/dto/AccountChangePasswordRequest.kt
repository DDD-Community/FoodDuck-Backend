package com.foodduck.foodduck.account.dto

import com.foodduck.foodduck.base.error.CustomException
import com.foodduck.foodduck.base.error.ErrorCode
import io.swagger.annotations.ApiModelProperty

data class AccountChangePasswordRequest(
    @ApiModelProperty(value = "1차 비밀번호", example = "Test12#$")
    val password: String,
    @ApiModelProperty(value = "2차 비밀번호", example = "Test12#$")
    val checkPassword: String
) {
    fun validateEqualPassword() {
        if (password != checkPassword) {
            throw CustomException(ErrorCode.NOT_EQUAL_PASSWORD_ERROR)
        }
    }
}
