package com.foodduck.foodduck.account.dto

import io.swagger.annotations.ApiModelProperty

data class AccountLoginRequest(
    @ApiModelProperty(value = "이메일", example = "foodduck@example.com")
    val email: String,
    @ApiModelProperty(value = "비밀번호", example = "Test12#$")
    val password: String
)
