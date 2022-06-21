
package com.foodduck.foodduck.menu.repository

import com.foodduck.foodduck.account.model.Account
import com.foodduck.foodduck.menu.model.Menu
import com.foodduck.foodduck.menu.model.MenuHistory
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface MenuHistoryRepository:JpaRepository<MenuHistory, Long>, MenuHistoryViewRepository {
    fun findByAccountAndMenuAndDeleteIsFalse(account: Account, menu: Menu): MenuHistory?

    fun findByAccountAndMenu_IdInAndDeleteIsFalse(account: Account, menuIds: List<Long>): List<MenuHistory>?

    @Modifying
    @Query("update MenuHistory mh set mh.delete = true where mh.menu = :menu")
    fun bulkDeleteTrue(@Param("menu") menu: Menu): Int
}