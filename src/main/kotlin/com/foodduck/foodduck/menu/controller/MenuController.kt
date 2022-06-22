package com.foodduck.foodduck.menu.controller

import com.foodduck.foodduck.account.model.Account
import com.foodduck.foodduck.account.model.AuthAccount
import com.foodduck.foodduck.base.message.MessageCode
import com.foodduck.foodduck.base.message.response.SimpleResponse
import com.foodduck.foodduck.menu.dto.MenuBasicRequestDto
import com.foodduck.foodduck.menu.dto.MenuBasicResponseDto
import com.foodduck.foodduck.menu.dto.MenuIdsDto
import com.foodduck.foodduck.menu.dto.MenuModifyRequestDto
import com.foodduck.foodduck.menu.service.MenuService
import com.foodduck.foodduck.menu.vo.DetailMenuVIewVo
import com.foodduck.foodduck.menu.vo.FindFavorMenuListVo
import com.foodduck.foodduck.menu.vo.MenuAlbumListVo
import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import io.swagger.annotations.ApiParam
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("api/v1/menus")
@Api(tags = ["메뉴 관련"])
class MenuController(
    private val menuService: MenuService
) {

    @ApiOperation(value = "메뉴 생성 - 토큰 필요")
    @PostMapping
    fun createMenu(
        @AuthAccount @ApiParam(hidden = true) account: Account,
        @ApiParam(value = "메뉴 생성 요청 값", required = true) request: MenuBasicRequestDto
    ): ResponseEntity<SimpleResponse<MenuBasicResponseDto>> {
        val response = menuService.postMenu(account, request)
        return ResponseEntity.status(HttpStatus.CREATED).body(SimpleResponse.of(HttpStatus.CREATED, MessageCode.MENU_CREATE, response))
    }

    @ApiOperation(value = "좋아요 클릭 - 토큰 필요")
    @PatchMapping("/favor/{menuId}")
    fun clickFavor(
        @AuthAccount @ApiParam(hidden = true) account: Account,
        @PathVariable @ApiParam(value = "메뉴 아이디", required = true) menuId: Long
    ): ResponseEntity<SimpleResponse<Unit>> {
        val message = menuService.clickFavor(account, menuId)
        return ResponseEntity.ok(SimpleResponse.of(HttpStatus.OK, message))
    }

    @ApiOperation(value = "디테일 메뉴 - 토큰 있을 때에는 히스토리 기록 없을 때에는 디테일 뷰만")
    @GetMapping("/{menuId}")
    fun detailMenu(
        @AuthAccount @ApiParam(hidden = true) account: Account?,
        @PathVariable @ApiParam(value = "메뉴 아이디", required = true)menuId: Long
    ): ResponseEntity<SimpleResponse<DetailMenuVIewVo>> {
        val menu: DetailMenuVIewVo = menuService.detailMenu(account, menuId)
        return ResponseEntity.ok(SimpleResponse.of(HttpStatus.OK, MessageCode.SELECT_OK, menu))
    }

    @ApiOperation(value = "메뉴 히스토리 - 토큰 필요")
    @GetMapping("/history")
    fun historyMenu(
        @AuthAccount @ApiParam(hidden = true) account: Account,
        @RequestParam(name = "menuId", required = false) @ApiParam(value = "메뉴 마지막 아이디", required = false) menuId: Long?,
        @RequestParam(name = "page-size") @ApiParam(value = "데이터 갯수", required = true) pageSize: Long
    ): ResponseEntity<SimpleResponse<List<MenuAlbumListVo>>> {
        val data = menuService.historyMenu(account, menuId, pageSize)
        return ResponseEntity.ok(SimpleResponse.of(HttpStatus.OK, MessageCode.SELECT_OK, data))
    }

    @ApiOperation(value = "좋아요 메뉴 - 토큰 필요")
    @GetMapping("/favor")
    fun favorMenu(
        @AuthAccount @ApiParam(hidden = true) account: Account,
        @RequestParam(name = "menuId", required = false) @ApiParam(value = "메뉴 마지막 아이디", required = false) menuId: Long?,
        @RequestParam(name = "page-size") @ApiParam(value = "데이터 갯수", required = true) pageSize: Long
    ): ResponseEntity<SimpleResponse<List<FindFavorMenuListVo>>> {
        val data = menuService.myFavorMenu(account, menuId, pageSize)
        return ResponseEntity.ok(SimpleResponse.of(HttpStatus.OK, MessageCode.SELECT_OK, data))
    }

    @ApiOperation(value = "내가 적은 메뉴들 - 토큰 필요")
    @GetMapping("/my")
    fun myMenu(
        @AuthAccount @ApiParam(hidden = true) account: Account,
        @RequestParam(name = "last-id", required = false) @ApiParam(value = "메뉴 마지막 아이디", required = false) menuId: Long?,
        @RequestParam(name = "page-size") @ApiParam(value = "데이터 갯수", required = true) pageSize: Long
    ): ResponseEntity<SimpleResponse<List<MenuAlbumListVo>>> {
        val data = menuService.myMenu(account, menuId, pageSize)
        return ResponseEntity.ok(SimpleResponse.of(HttpStatus.OK, MessageCode.SELECT_OK, data))
    }

    @ApiOperation(value = "메뉴 수정 - 토큰 필요")
    @PutMapping("/{menuId}")
    fun modifyMenu(
        @AuthAccount @ApiParam(hidden = true) account: Account,
        @PathVariable @ApiParam(value = "메뉴 아이디", required = true) menuId: Long,
        @ApiParam(value = "메뉴 수정 요청값", required = true) request: MenuModifyRequestDto
    ): ResponseEntity<SimpleResponse<MenuBasicResponseDto>> {
        val data = menuService.modifyMenu(account, menuId, request)
        return ResponseEntity.ok(SimpleResponse.of(HttpStatus.OK, MessageCode.MODIFY, data))
    }

    @ApiOperation(value = "메뉴 기록 삭제 - 토큰 필요")
    @DeleteMapping("/history")
    fun deleteMyMenuHistory(
        @AuthAccount @ApiParam(hidden = true) account: Account,
        @RequestBody @ApiParam(value = "메뉴 기록 삭제 아이디들", required = true)request: MenuIdsDto
    ): ResponseEntity<SimpleResponse<Unit>> {
        menuService.deleteMyMenuHistory(account, request)
        return ResponseEntity.ok(SimpleResponse.of(HttpStatus.OK, MessageCode.DELETE))
    }

    @ApiOperation(value = "메뉴 삭제 - 토큰 필요")
    @DeleteMapping("/{menuId}")
    fun deleteMyMenu(
        @AuthAccount @ApiParam(hidden = true) account: Account,
        @PathVariable @ApiParam(value = "메뉴 아이디", required = true) menuId: Long
    ): ResponseEntity<SimpleResponse<Unit>> {
        menuService.deleteMyMenu(account, menuId)
        return ResponseEntity.ok(SimpleResponse.of(HttpStatus.OK, MessageCode.DELETE))
    }
}