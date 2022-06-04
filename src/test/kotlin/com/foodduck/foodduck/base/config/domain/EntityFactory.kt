package com.foodduck.foodduck.base.config.domain

import com.foodduck.foodduck.account.model.Account

class EntityFactory {
    companion object {
        fun accountTemplate() = Account(
            id= 1L,
            nickname="foodduck",
            email="foodduck@example.com",
            password = "Test12#$"
        )

        fun accountTemplateForReal() = Account(
            nickname="foodduck",
            email="foodduck@example.com",
            password="Test12#$"
        )
    }
}