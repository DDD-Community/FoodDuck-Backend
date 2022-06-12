package com.foodduck.foodduck.menu.repository

import com.foodduck.foodduck.menu.model.FavorMenu
import org.springframework.data.jpa.repository.JpaRepository

interface FavorMenuRepository:JpaRepository<FavorMenu, Long> {
}