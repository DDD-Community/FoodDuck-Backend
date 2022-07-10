package com.foodduck.foodduck.menu.service

import com.foodduck.foodduck.account.model.Account
import com.foodduck.foodduck.base.error.CustomException
import com.foodduck.foodduck.base.error.ErrorCode
import com.foodduck.foodduck.menu.dto.CommentModifyRequestDto
import com.foodduck.foodduck.menu.dto.CommentRequestDto
import com.foodduck.foodduck.menu.model.Comment
import com.foodduck.foodduck.menu.model.Menu
import com.foodduck.foodduck.menu.repository.CommentRepository
import com.foodduck.foodduck.menu.repository.MenuRepository
import com.foodduck.foodduck.menu.vo.FindCommentListVo
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class CommentService(
    private val commentRepository: CommentRepository,
    private val menuRepository: MenuRepository
) {

    fun createComment(account: Account, request: CommentRequestDto): Long? {
        val menu: Menu = menuRepository.findByIdAndDeleteIsFalse(request.menuId) ?: throw CustomException(ErrorCode.MENU_NOT_FOUND_ERROR)
        val parentComment: Comment? = commentRepository.findByIdAndDeleteIsFalse(request.commentId)
        val comment: Comment = request.toComment(account, menu, parentComment)
        val saveComment = commentRepository.save(comment)
        return saveComment.id
    }

    fun putComment(account: Account, commentId: Long, request: CommentModifyRequestDto) {
        val comment = commentRepository.findByAccountAndIdAndDeleteIsFalse(account, commentId) ?: throw CustomException(ErrorCode.COMMENT_NOT_FOUND)
        request.updateComment(comment)
    }

    fun deleteComment(account: Account, commentId: Long) {
        val comment = commentRepository.findByAccountAndIdAndDeleteIsFalse(account, commentId) ?: throw CustomException(ErrorCode.COMMENT_NOT_FOUND)
        comment.remove()
    }

    @Transactional(readOnly = true)
    fun countComments(menuId: Long): Long {
        return commentRepository.countByMenu_IdAndDeleteIsFalse(menuId)
    }

    @Transactional(readOnly = true)
    fun findFirstDepthComments(menuId: Long, lastId: Long?, pageSize: Long): List<FindCommentListVo> {
        return commentRepository.findFirstDepthCommentList(menuId, lastId, pageSize)
    }

    @Transactional(readOnly = true)
    fun findSecondDepthComments(menuId: Long, commentId: Long, lastId: Long?, pageSize: Long): List<FindCommentListVo> {
        return commentRepository.findSecondDepthCommentList(commentId, menuId, lastId, pageSize)
    }
}