package com.foodduck.foodduck.menu.controller

import com.foodduck.foodduck.account.model.Account
import com.foodduck.foodduck.account.model.AuthAccount
import com.foodduck.foodduck.base.message.MessageCode
import com.foodduck.foodduck.base.message.response.SimpleResponse
import com.foodduck.foodduck.menu.dto.CommentModifyRequestDto
import com.foodduck.foodduck.menu.dto.CommentRequestDto
import com.foodduck.foodduck.menu.service.CommentService
import com.foodduck.foodduck.menu.vo.FindCommentListVo
import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import io.swagger.annotations.ApiParam
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("api/v1/comments")
@Api(tags = ["댓글 관련"])
class CommentController(
    private val commentService: CommentService
) {
    @ApiOperation(value = "댓글 생성 - 토큰 필요")
    @PostMapping
    fun createComment(
        @AuthAccount @ApiParam(hidden = true) account: Account,
        @ApiParam(value = "댓글 생성 요청 값") request: CommentRequestDto
    ): ResponseEntity<SimpleResponse<Long>> {
        val commentId = commentService.createComment(account, request)
        return ResponseEntity.status(HttpStatus.CREATED).body(SimpleResponse.of(HttpStatus.CREATED, MessageCode.SAVE, commentId))
    }

    @ApiOperation(value = "댓글 수정 - 토큰 필요")
    @PatchMapping("/{commentId}")
    fun putComment(
        @AuthAccount @ApiParam(hidden = true) account: Account,
        @PathVariable @ApiParam(value = "댓글 아이디") commentId: Long,
        @RequestBody @ApiParam(value = "수정 내용") request: CommentModifyRequestDto
    ): ResponseEntity<SimpleResponse<Unit>> {
        commentService.putComment(account, commentId, request)
        return ResponseEntity.ok(SimpleResponse.of(HttpStatus.OK, MessageCode.MODIFY))
    }

    @ApiOperation(value = "댓글 삭제 - 토큰 필요")
    @DeleteMapping("/{commentId}")
    fun deleteComment(
        @AuthAccount @ApiParam(hidden = true) account: Account,
        @PathVariable @ApiParam(value = "댓글 아이디") commentId: Long
    ): ResponseEntity<SimpleResponse<Unit>> {
        commentService.deleteComment(account, commentId)
        return ResponseEntity.ok(SimpleResponse.of(HttpStatus.OK, MessageCode.DELETE))
    }

    @ApiOperation(value = "해당 메뉴 첫 번째 깊이 댓글들")
    @GetMapping
    fun findFirstDepthComments(
        @RequestParam(name = "menu-id", required = true) @ApiParam(value = "메뉴 아이디", required = false) menuId: Long,
        @RequestParam(name = "last-id", required = false) @ApiParam(value = "댓글 마지막 아이디", required = false) lastId: Long?,
        @RequestParam(name = "page-size") @ApiParam(value = "데이터 갯수", required = true) pageSize: Long
    ): ResponseEntity<SimpleResponse<List<FindCommentListVo>>> {
        val data = commentService.findFirstDepthComments(menuId, lastId, pageSize)
        return ResponseEntity.ok(SimpleResponse.of(HttpStatus.OK, MessageCode.SELECT_OK, data))
    }


    @ApiOperation(value = "해당 메뉴 두 번째 깊이 댓글들")
    @GetMapping("/{commentId}")
    fun findSecondDepthComments(
        @PathVariable @ApiParam(name = "댓글 아이디") commentId: Long,
        @RequestParam(name = "menu-id", required = true) @ApiParam(value = "메뉴 아이디", required = false) menuId: Long,
        @RequestParam(name = "last-id", required = false) @ApiParam(value = "댓글 마지막 아이디", required = false) lastId: Long?,
        @RequestParam(name = "page-size") @ApiParam(value = "데이터 갯수", required = true) pageSize: Long
    ): ResponseEntity<SimpleResponse<List<FindCommentListVo>>> {
        val data = commentService.findSecondDepthComments(menuId, commentId, lastId, pageSize)
        return ResponseEntity.ok(SimpleResponse.of(HttpStatus.OK, MessageCode.SELECT_OK, data))
    }

    @ApiOperation(value = "메뉴 댓글 갯수")
    @GetMapping("/count")
    fun countComments(
        @RequestParam(name = "menu-id", required = true) @ApiParam(value = "메뉴 아이디", required = false) menuId: Long
    ): ResponseEntity<SimpleResponse<Long>> {
        val count = commentService.countComments(menuId)
        return ResponseEntity.ok(SimpleResponse.of(HttpStatus.OK, MessageCode.SELECT_OK, count))
    }
}