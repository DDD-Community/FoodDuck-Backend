package com.foodduck.foodduck.menu.dto

import io.swagger.annotations.ApiModelProperty

data class MenuIdsDto(
    @ApiModelProperty(value = "메뉴 아이디들", required = true)
    val menuIds: List<Long>
)


