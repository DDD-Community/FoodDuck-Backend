
package com.foodduck.foodduck.menu.repository

import com.foodduck.foodduck.account.model.Account
import com.foodduck.foodduck.menu.model.Menu
import com.foodduck.foodduck.menu.model.MenuHistory
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface MenuHistoryRepository:JpaRepository<MenuHistory, Long>, MenuHistoryViewRepository {
    @Query("select mh from MenuHistory mh join mh.menu where mh.delete = false and mh.menu.delete = false and mh.account = :account and mh.menu = :menu")
    fun findMenuHistoryByAccountAndMenu(@Param("account") account: Account, @Param("menu") menu: Menu): MenuHistory?

    @Query("select mh from MenuHistory mh join mh.menu where mh.delete = false and mh.menu.delete = false and mh.account = :account and mh.menu.id in :menuIds order by mh.menu.id asc ")
    fun findMenuHistoryListByAccountAndMenuIds(@Param("account") account: Account, @Param("menuIds") menuIds: List<Long>): List<MenuHistory>?

    @Modifying
    @Query("update MenuHistory mh set mh.delete = true where mh.menu = :menu")
    fun bulkDeleteTrue(@Param("menu") menu: Menu): Int
}