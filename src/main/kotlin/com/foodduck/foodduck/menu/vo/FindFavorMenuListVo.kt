package com.foodduck.foodduck.menu.vo

data class FindFavorMenuListVo(
    val menuId: Long,
    val nickname: String,
    val url: String,
    val title: String,
    val body: String,
    val count: Long,
)
