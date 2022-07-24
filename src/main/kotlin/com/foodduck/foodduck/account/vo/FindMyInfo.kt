package com.foodduck.foodduck.account.vo

data class FindMyInfo(
    val nickname: String,
    val myFavorCount: Long,
    val myMenuCount: Long,
    val profile: String
)
