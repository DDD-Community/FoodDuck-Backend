package com.foodduck.foodduck.base.config.security.token

data class TokenDto(
    val accessToken: String,
    val refreshToken: String
) {

}