package com.foodduck.foodduck.base.config.domain

import com.foodduck.foodduck.account.model.Account
import com.foodduck.foodduck.menu.model.Menu

class EntityFactory {
    companion object {
        fun accountTemplate(id: Long = 1L, nickname: String = "foodduck") = Account(
            id= id,
            nickname=nickname,
            email="foodduck@example.com",
            password = "Test12#$"
        )

        fun accountTemplateForReal() = Account(
            nickname="foodduck",
            email="foodduck@example.com",
            password="Test12#$"
        )

        fun menuTemplate(account: Account, favorCount: Long = 0L, id: Long = 1L) = Menu(
            id = id,
            title = "나만의 조합",
            body = "이겁니다",
            account = account,
            url = "image.png",
            favorCount = favorCount
        )
    }
}