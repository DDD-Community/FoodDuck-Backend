package com.foodduck.foodduck.menu.repository

import com.foodduck.foodduck.account.model.Account
import com.foodduck.foodduck.menu.model.Menu
import com.foodduck.foodduck.menu.model.MenuHistory
import org.springframework.data.jpa.repository.JpaRepository

interface MenuHistoryRepository:JpaRepository<MenuHistory,Long>, MenuHistoryViewRepository {
    fun findByAccountAndMenu(account: Account, menu: Menu):MenuHistory?
}