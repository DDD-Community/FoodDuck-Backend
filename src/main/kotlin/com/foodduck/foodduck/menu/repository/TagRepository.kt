
package com.foodduck.foodduck.menu.repository

import com.foodduck.foodduck.menu.model.Tag
import org.springframework.data.jpa.repository.JpaRepository

interface TagRepository: JpaRepository<Tag, Long> {
    fun existsByTitle(title: String): Boolean
    fun findByTitle(title: String): Tag?
    fun findByTitleIn(titles: List<String>): List<Tag>?
}