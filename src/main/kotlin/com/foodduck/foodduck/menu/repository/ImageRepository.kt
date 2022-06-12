package com.foodduck.foodduck.menu.repository

import com.foodduck.foodduck.menu.model.Image
import org.springframework.data.jpa.repository.JpaRepository

interface ImageRepository:JpaRepository<Image, Long> {
}