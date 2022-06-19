package com.foodduck.foodduck.menu.repository

import com.foodduck.foodduck.menu.model.Menu
import com.foodduck.foodduck.menu.model.TagMenu
import org.springframework.data.jpa.repository.JpaRepository

interface TagMenuRepository:JpaRepository<TagMenu, Long>, TagMenuViewRepository {
    fun findByMenu(menu: Menu): List<TagMenu>?
}