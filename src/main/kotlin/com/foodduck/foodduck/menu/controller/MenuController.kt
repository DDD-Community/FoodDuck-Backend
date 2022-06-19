package com.foodduck.foodduck.menu.controller

import com.foodduck.foodduck.account.model.Account
import com.foodduck.foodduck.account.model.AuthAccount
import com.foodduck.foodduck.base.message.MessageCode
import com.foodduck.foodduck.base.message.response.SimpleResponse
import com.foodduck.foodduck.menu.dto.MenuCreateRequestDto
import com.foodduck.foodduck.menu.dto.MenuCreateResponseDto
import com.foodduck.foodduck.menu.model.Menu
import com.foodduck.foodduck.menu.service.MenuService
import com.foodduck.foodduck.menu.vo.DetailMenuVIewVo
import com.foodduck.foodduck.menu.vo.FindFavorMenuListVo
import com.foodduck.foodduck.menu.vo.FindMenuHistoryListVo
import com.foodduck.foodduck.menu.vo.FindMenuListVo
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
    fun createMenu(@AuthAccount account: Account, @RequestBody request: MenuCreateRequestDto): ResponseEntity<SimpleResponse<MenuCreateResponseDto>> {
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
        @RequestParam(name = "last-id", required = false) menuHistoryId: Long?,
        @RequestParam(name = "page-size") pageSize: Long
    ): ResponseEntity<SimpleResponse<List<FindMenuHistoryListVo>>> {
        val data = menuService.historyMenu(account, menuHistoryId, pageSize)
        return ResponseEntity.ok(SimpleResponse.of(HttpStatus.OK, MessageCode.SELECT_OK, data))
    }

    @ApiOperation(value = "좋아요 메뉴")
    @GetMapping("/favor")
    fun favorMenu(
        @AuthAccount account: Account,
        @RequestParam(name = "last-id", required = false) favorMenuId: Long?,
        @RequestParam(name = "page-size") pageSize: Long
    ): ResponseEntity<SimpleResponse<List<FindFavorMenuListVo>>> {
        val data = menuService.myFavorMenu(account, favorMenuId, pageSize)
        return ResponseEntity.ok(SimpleResponse.of(HttpStatus.OK, MessageCode.SELECT_OK, data))
    }
}