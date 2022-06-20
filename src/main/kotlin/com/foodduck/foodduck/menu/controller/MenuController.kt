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
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("api/v1/menus")
@Api(tags = ["메뉴 관련"])
class MenuController(
    private val menuService: MenuService
) {

    @ApiOperation(value = "메뉴 생성")
    @PostMapping
    fun createMenu(@AuthAccount account: Account, request: MenuBasicRequestDto): ResponseEntity<SimpleResponse<MenuBasicResponseDto>> {
        val response = menuService.postMenu(account, request)
        return ResponseEntity.status(HttpStatus.CREATED).body(SimpleResponse.of(HttpStatus.CREATED, MessageCode.MENU_CREATE, response))
    }

    @ApiOperation(value = "좋아요 클릭")
    @PatchMapping("/favor/{menuId}")
    fun clickFavor(@AuthAccount account: Account, @PathVariable menuId: Long): ResponseEntity<SimpleResponse<Unit>> {
        val message = menuService.clickFavor(account, menuId)
        return ResponseEntity.ok(SimpleResponse.of(HttpStatus.OK, message))
    }

    @ApiOperation(value = "디테일 메뉴")
    @GetMapping("/{menuId}")
    fun detailMenu(@AuthAccount account: Account?, @PathVariable menuId: Long): ResponseEntity<SimpleResponse<DetailMenuVIewVo>> {
        val menu: DetailMenuVIewVo = menuService.detailMenu(account, menuId)
        return ResponseEntity.ok(SimpleResponse.of(HttpStatus.OK, MessageCode.SELECT_OK, menu))
    }

    @ApiOperation(value = "메뉴 히스토리")
    @GetMapping("/history")
    fun historyMenu(
        @AuthAccount account: Account,
        @RequestParam(name = "menuId", required = false) menuId: Long?,
        @RequestParam(name = "page-size") pageSize: Long
    ): ResponseEntity<SimpleResponse<List<MenuAlbumListVo>>> {
        val data = menuService.historyMenu(account, menuId, pageSize)
        return ResponseEntity.ok(SimpleResponse.of(HttpStatus.OK, MessageCode.SELECT_OK, data))
    }

    @ApiOperation(value = "좋아요 메뉴")
    @GetMapping("/favor")
    fun favorMenu(
        @AuthAccount account: Account,
        @RequestParam(name = "menuId", required = false) menuId: Long?,
        @RequestParam(name = "page-size") pageSize: Long
    ): ResponseEntity<SimpleResponse<List<FindFavorMenuListVo>>> {
        val data = menuService.myFavorMenu(account, menuId, pageSize)
        return ResponseEntity.ok(SimpleResponse.of(HttpStatus.OK, MessageCode.SELECT_OK, data))
    }

    @ApiOperation(value = "내가 적은 메뉴들")
    @GetMapping("/my")
    fun myMenu(
        @AuthAccount account: Account,
        @RequestParam(name = "last-id", required = false) menuId: Long?,
        @RequestParam(name = "page-size") pageSize: Long
    ): ResponseEntity<SimpleResponse<List<MenuAlbumListVo>>> {
        val data = menuService.myMenu(account, menuId, pageSize)
        return ResponseEntity.ok(SimpleResponse.of(HttpStatus.OK, MessageCode.SELECT_OK, data))
    }

    @ApiOperation(value = "메뉴 수정")
    @PutMapping("/{menuId}")
    fun modifyMenu(
        @AuthAccount account: Account,
        @PathVariable menuId: Long,
        request: MenuModifyRequestDto
    ): ResponseEntity<SimpleResponse<MenuBasicResponseDto>> {
        val data = menuService.modifyMenu(account, menuId, request)
        return ResponseEntity.ok(SimpleResponse.of(HttpStatus.OK, MessageCode.MODIFY, data))
    }

    @ApiOperation(value = "메뉴 기록 삭제")
    @DeleteMapping("/history")
    fun deleteMyMenuHistory(
        @AuthAccount account: Account,
        @RequestBody request: MenuIdsDto
    ): ResponseEntity<SimpleResponse<Unit>> {
        menuService.deleteMyMenuHistory(account, request)
        return ResponseEntity.ok(SimpleResponse.of(HttpStatus.OK, MessageCode.DELETE))
    }

    @ApiOperation(value = "미뉴 삭제")
    @DeleteMapping("/{menuId}")
    fun deleteMyMenu(
        @AuthAccount account: Account,
        @PathVariable menuId: Long
    ): ResponseEntity<SimpleResponse<Unit>> {
        menuService.deleteMyMenu(account, menuId)
        return ResponseEntity.ok(SimpleResponse.of(HttpStatus.OK, MessageCode.DELETE))
    }


}