package com.foodduck.foodduck.menu.repository

import com.foodduck.foodduck.account.model.Account
import com.foodduck.foodduck.menu.model.Comment
import org.springframework.data.jpa.repository.JpaRepository

interface CommentRepository: JpaRepository<Comment, Long>, CommentViewRepository {
    fun findByIdAndDeleteIsFalse(commentId: Long): Comment?
    fun findByAccountAndIdAndDeleteIsFalse(account: Account, commentId: Long): Comment?
    fun countByMenu_IdAndDeleteIsFalse(menuId: Long): Long
}