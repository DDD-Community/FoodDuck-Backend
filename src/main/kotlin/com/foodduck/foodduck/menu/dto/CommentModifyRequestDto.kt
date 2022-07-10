package com.foodduck.foodduck.menu.dto

import com.foodduck.foodduck.menu.model.Comment

data class CommentModifyRequestDto(
    val body: String
) {
    fun updateComment(comment: Comment) {
        comment.body = body
    }
}