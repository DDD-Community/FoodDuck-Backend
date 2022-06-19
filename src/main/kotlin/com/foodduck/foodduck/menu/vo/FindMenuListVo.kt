package com.foodduck.foodduck.menu.vo

data class FindMenuListVo(
    val menuId: Long,
    val nickname: String,
    val url: String,
    val title: String,
    val body: String,
    val count: Long,
)
