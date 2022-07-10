package com.foodduck.foodduck.menu.service

import com.foodduck.foodduck.base.config.domain.EntityFactory
import com.foodduck.foodduck.menu.dto.CommentModifyRequestDto
import com.foodduck.foodduck.menu.dto.CommentRequestDto
import com.foodduck.foodduck.menu.repository.CommentRepository
import com.foodduck.foodduck.menu.repository.MenuRepository
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import org.assertj.core.api.Assertions
import org.assertj.core.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.data.repository.findByIdOrNull
import javax.xml.stream.events.Comment

internal class CommentServiceTest {
    private lateinit var commentService: CommentService

    @MockK
    private lateinit var commentRepository: CommentRepository

    @MockK
    private lateinit var menuRepository: MenuRepository

    @BeforeEach
    fun setUp() {
        MockKAnnotations.init(this)
        commentService = CommentService(commentRepository, menuRepository)
    }

    @Test
    fun `댓글을 남긴다 메뉴에`() {
        val account = EntityFactory.accountTemplate()
        val menu = EntityFactory.menuTemplate(account = account)
        val request = CommentRequestDto(body = "맛있겠다", menuId = 1L, commentId = 0L)
        val comment = EntityFactory.commentTemplate(account, menu, null)
        every { menuRepository.findByIdAndDeleteIsFalse(1L) }.returns(menu)
        every { commentRepository.findByIdAndDeleteIsFalse(0L) }.returns(null)
        every { commentRepository.save(any()) }.returns(comment)

        val saveCommentId = commentService.createComment(account, request)
        assertThat(comment.id).isEqualTo(saveCommentId)
    }

    @Test
    fun `대댓글을 생성한다`() {
        val account = EntityFactory.accountTemplate()
        val menu = EntityFactory.menuTemplate(account = account)
        val request = CommentRequestDto(body = "맛있겠다", menuId = 1L, commentId = 0L)
        val parentComment = EntityFactory.commentTemplate(account, menu, null)
        val comment = EntityFactory.commentTemplate(account, menu, parentComment, 2L)
        every { menuRepository.findByIdAndDeleteIsFalse(1L) }.returns(menu)
        every { commentRepository.findByIdAndDeleteIsFalse(0L) }.returns(parentComment)
        every { commentRepository.save(any()) }.returns(comment)

        val saveCommentId = commentService.createComment(account, request)
        assertThat(comment.id).isEqualTo(saveCommentId)
    }

    @Test
    fun `댓글을 수정한다`() {
        val account = EntityFactory.accountTemplate()
        val menu = EntityFactory.menuTemplate(account = account)
        val request = CommentModifyRequestDto(body = "modify body")
        val comment = EntityFactory.commentTemplate(account, menu, null)
        every { commentRepository.findByAccountAndIdAndDeleteIsFalse(any(), 1L) }.returns(comment)
        commentService.putComment(account, 1L, request)
        assertThat(comment.body).isEqualTo(request.body)
    }

    @Test
    fun `댓글을 삭제한다`() {
        val account = EntityFactory.accountTemplate()
        val menu = EntityFactory.menuTemplate(account = account)
        val comment = EntityFactory.commentTemplate(account, menu, null)
        every { commentRepository.findByAccountAndIdAndDeleteIsFalse(any(), 1L) }.returns(comment)
        commentService.deleteComment(account, 1L)
        assertThat(comment.delete).isTrue
    }
}