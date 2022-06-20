package com.foodduck.foodduck.menu.repository

import com.foodduck.foodduck.account.model.Account
import com.foodduck.foodduck.menu.model.Menu
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface MenuRepository:JpaRepository<Menu, Long>, MenuViewRepository {
    fun findByIdAndDeleteIsFalse(menuId:Long): Menu?

    @Query("select m from Menu m join fetch m.tagMenu where m.id = :menuId and m.account = :account")
    fun findByIdAndDeleteIsFalseAndAccount(@Param("menuId") menuId:Long, @Param("account") account: Account): Menu?
}