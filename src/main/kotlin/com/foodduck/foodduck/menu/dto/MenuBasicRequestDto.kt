package com.foodduck.foodduck.menu.dto

import com.foodduck.foodduck.account.model.Account
import com.foodduck.foodduck.menu.model.Menu
import org.springframework.web.multipart.MultipartFile

data class MenuBasicRequestDto(
    val image:MultipartFile,
    val title: String,
    val body: String,
    val tags: List<String>
) {
    fun toMenu(account: Account, url: String): Menu {
        return Menu(title = title, body = body, account = account, favorCount = 0L, url=url)
    }
}