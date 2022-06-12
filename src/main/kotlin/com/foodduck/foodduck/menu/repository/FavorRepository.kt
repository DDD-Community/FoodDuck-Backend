package com.foodduck.foodduck.menu.repository

import com.foodduck.foodduck.menu.model.Favor
import org.springframework.data.jpa.repository.JpaRepository

interface FavorRepository:JpaRepository<Favor, Long> {
}