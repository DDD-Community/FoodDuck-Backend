package com.foodduck.foodduck.base.config.domain

import com.foodduck.foodduck.account.model.Account
import com.foodduck.foodduck.menu.model.Comment
import com.foodduck.foodduck.menu.model.Menu

class EntityFactory {
    companion object {
        fun accountTemplate(id: Long = 1L, nickname: String = "foodduck", profile: String = "") = Account(
            id= id,
            nickname=nickname,
            email="foodduck@example.com",
            password = "Test12#$",
            profile = profile
        )

        fun accountTemplateForReal(nickname: String = "foodduck", profile: String = "") = Account(
            nickname=nickname,
            email="foodduck@example.com",
            password="Test12#$",
            profile = profile
        )

        fun menuTemplate(account: Account, favorCount: Long = 0L, id: Long = 1L) = Menu(
            id = id,
            title = "나만의 조합",
            body = "이겁니다",
            account = account,
            url = "image.png",
            favorCount = favorCount
        )

        fun menuTemplateForReal(account: Account, favorCount: Long = 0L) = Menu(
            title = "나만의 조합",
            body = "이겁니다",
            account = account,
            url = "image.png",
            favorCount = favorCount
        )

        fun commentTemplate(account: Account, menu: Menu, parentComment: Comment?, id: Long = 1L) = Comment(
            id = id,
            account = account,
            menu = menu,
            parent = parentComment,
            body = "맛있겠다."
        )

        fun commentTemplateForReal(account: Account, menu: Menu, parentComment: Comment?, body: String = "맛있겠다.") = Comment(
            account = account,
            menu = menu,
            parent = parentComment,
            body = body
        )
    }
}