package com.foodduck.foodduck.menu.controller

import com.foodduck.foodduck.base.message.MessageCode
import com.foodduck.foodduck.base.message.response.SimpleResponse
import com.foodduck.foodduck.menu.service.MenuService
import com.foodduck.foodduck.menu.vo.FindMenuListVo
import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import io.swagger.annotations.ApiParam
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("api/v1/tag-menus")
@Api(tags = ["메뉴 관련"])
class TagMenuController(
    private val menuService: MenuService
) {

    @ApiOperation(value = "태그 메뉴 리스트")
    @GetMapping
    fun listMenu(
        @RequestParam(name = "tag-name") @ApiParam(value = "태그 이름 (전체는 INIT_ALL)", required = true) tagName: String,
        @RequestParam(name = "last-id", required = false) @ApiParam(value = "메뉴 마지막 아이디", required = false) menuId: Long?,
        @RequestParam(name = "order-by") @ApiParam(value = "정렬 [id, -id, favorCount, -favorCount] 가 존재하며 '-'는 내림차순", required = true) orderBy: String,
        @RequestParam(name = "page-size") @ApiParam(value = "데이터 갯수", required = true) pageSize: Long
    ): ResponseEntity<SimpleResponse<List<FindMenuListVo>>> {
        val data = menuService.listMenu(tagName, menuId, orderBy, pageSize)
        return ResponseEntity.ok(SimpleResponse.of(HttpStatus.OK, MessageCode.SELECT_OK, data))
    }
}