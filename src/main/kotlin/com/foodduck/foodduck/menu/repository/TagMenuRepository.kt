package com.foodduck.foodduck.menu.repository

import com.foodduck.foodduck.menu.model.Menu
import com.foodduck.foodduck.menu.model.TagMenu
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface TagMenuRepository:JpaRepository<TagMenu, Long>, TagMenuViewRepository {
    fun findByMenuAndDeleteIsFalse(menu: Menu): List<TagMenu>

    @Modifying
    @Query("update TagMenu tm set tm.delete = true where tm.menu = :menu")
    fun bulkDeleteTrue(@Param("menu") menu:Menu): Int
}