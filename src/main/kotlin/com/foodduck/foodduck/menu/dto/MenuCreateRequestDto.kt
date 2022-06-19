package com.foodduck.foodduck.menu.dto

import com.foodduck.foodduck.account.model.Account
import com.foodduck.foodduck.menu.model.Menu

data class MenuCreateRequestDto(
    val image:String,
    val title: String,
    val body: String,
    val tags: List<String>
) {
    fun toMenu(account: Account): Menu {
        return Menu(title = title, body = body, account = account, favorCount = 0L, url=image)
    }
}