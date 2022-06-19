package com.foodduck.foodduck.menu.repository

import com.foodduck.foodduck.menu.model.Menu
import org.springframework.data.jpa.repository.JpaRepository

interface MenuRepository:JpaRepository<Menu, Long> {
    fun findByIdAndDeleteIsFalse(menuId:Long): Menu?
}