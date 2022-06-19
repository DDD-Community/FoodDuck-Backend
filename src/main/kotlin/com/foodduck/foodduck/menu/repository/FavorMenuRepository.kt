package com.foodduck.foodduck.menu.repository

import com.foodduck.foodduck.account.model.Account
import com.foodduck.foodduck.menu.model.FavorMenu
import com.foodduck.foodduck.menu.model.Menu
import org.springframework.data.jpa.repository.JpaRepository

interface FavorMenuRepository:JpaRepository<FavorMenu, Long>, FavorMenuViewRepository {
    fun findByAccountAndMenu(account:Account, menu:Menu): FavorMenu?
}