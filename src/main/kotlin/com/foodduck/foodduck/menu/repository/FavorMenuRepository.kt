package com.foodduck.foodduck.menu.repository

import com.foodduck.foodduck.account.model.Account
import com.foodduck.foodduck.menu.model.FavorMenu
import com.foodduck.foodduck.menu.model.Menu
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface FavorMenuRepository:JpaRepository<FavorMenu, Long>, FavorMenuViewRepository {
    fun findByAccountAndMenuAndDeleteIsFalse(account:Account, menu:Menu): FavorMenu?

    @Modifying
    @Query("update FavorMenu fm set fm.delete = true where fm.menu = :menu")
    fun bulkDeleteTrue(@Param("menu") menu:Menu): Int
}