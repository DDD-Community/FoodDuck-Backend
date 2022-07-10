package com.foodduck.foodduck.menu.repository

import com.foodduck.foodduck.menu.vo.FindCommentListVo

interface CommentViewRepository {

    fun findFirstDepthCommentList(menuId: Long, lastId: Long?, pageSize: Long): List<FindCommentListVo>

    fun findSecondDepthCommentList(commentId: Long, menuId: Long, lastId: Long?, pageSize: Long): List<FindCommentListVo>
}