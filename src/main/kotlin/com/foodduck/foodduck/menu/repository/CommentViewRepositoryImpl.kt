package com.foodduck.foodduck.menu.repository

import com.foodduck.foodduck.account.model.QAccount.*
import com.foodduck.foodduck.menu.model.QComment.*
import com.foodduck.foodduck.menu.vo.FindCommentListVo
import com.querydsl.core.types.Projections
import com.querydsl.core.types.dsl.BooleanExpression
import com.querydsl.jpa.impl.JPAQueryFactory

class CommentViewRepositoryImpl(
    private val query: JPAQueryFactory
): CommentViewRepository {

    override fun findFirstDepthCommentList(menuId: Long, lastId: Long?, pageSize: Long): List<FindCommentListVo> {
        return query
            .select(Projections.constructor(FindCommentListVo::class.java, comment.account.nickname, comment.account.id, comment.createdAt, comment.body))
            .from(comment)
            .join(comment.account, account)
            .where(onlyFirstDepthComments(),commentDeleteFalse(),ltLastCommentId(lastId), comment.menu.id.eq(menuId))
            .limit(pageSize)
            .orderBy(comment.id.desc())
            .fetch()
    }

    override fun findSecondDepthCommentList(commentId: Long, menuId: Long, lastId: Long?, pageSize: Long): List<FindCommentListVo> {
        return query
            .select(Projections.constructor(FindCommentListVo::class.java, comment.account.nickname, comment.account.id, comment.createdAt, comment.body))
            .from(comment)
            .join(comment.account, account)
            .where(comment.parent.id.eq(commentId), commentDeleteFalse(),ltLastCommentId(lastId))
            .limit(pageSize)
            .orderBy(comment.id.desc())
            .fetch()
    }

    private fun onlyFirstDepthComments(): BooleanExpression? {
        return comment.parent.isNull
    }

    private fun commentDeleteFalse(): BooleanExpression? {
        return comment.delete.isFalse
    }

    private fun ltLastCommentId(lastId: Long?): BooleanExpression? {
        if (lastId == null) {
            return null
        }
        return comment.id.lt(lastId)
    }
}