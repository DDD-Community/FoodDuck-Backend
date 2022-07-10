package com.foodduck.foodduck.menu.dto

import com.foodduck.foodduck.account.model.Account
import com.foodduck.foodduck.menu.model.Comment
import com.foodduck.foodduck.menu.model.Menu
import io.swagger.annotations.ApiModelProperty

data class CommentRequestDto(
    @ApiModelProperty(value = "댓글 내용", required = true)
    val body: String,
    @ApiModelProperty(value = "메뉴 아이디", required = true)
    val menuId: Long,
    @ApiModelProperty(value = "댓글 아이디(없을 때에는 0)", required = true)
    val commentId: Long
) {
    fun toComment(account: Account, menu: Menu, parentComment: Comment?): Comment {
        val comment = Comment(body = body, menu = menu, account = account, parent = parentComment)
        parentComment?.addChildComment(comment)
        return comment
    }
}

