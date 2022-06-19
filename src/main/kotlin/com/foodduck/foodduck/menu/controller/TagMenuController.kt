package com.foodduck.foodduck.menu.controller

import com.foodduck.foodduck.base.message.MessageCode
import com.foodduck.foodduck.base.message.response.SimpleResponse
import com.foodduck.foodduck.menu.service.MenuService
import com.foodduck.foodduck.menu.vo.FindMenuListVo
import io.swagger.annotations.Api
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

    @GetMapping
    fun listMenu(
        @RequestParam(name = "tag-name") tagName: String,
        @RequestParam(name = "last-id", required = false) menuId: Long?,
        @RequestParam(name = "order-by") orderBy: String,
        @RequestParam(name = "page-size") pageSize: Long
    ): ResponseEntity<SimpleResponse<List<FindMenuListVo>>> {
        val data = menuService.listMenu(tagName, menuId, orderBy, pageSize)
        return ResponseEntity.ok(SimpleResponse.of(HttpStatus.OK, MessageCode.SELECT_OK, data))
    }

}