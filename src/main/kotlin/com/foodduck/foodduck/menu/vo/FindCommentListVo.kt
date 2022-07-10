package com.foodduck.foodduck.menu.vo

import java.time.LocalDateTime

data class FindCommentListVo(
    val accountName: String,
    val accountId: Long,
    val commentCreatedAt: LocalDateTime,
    val commentBody: String
)
