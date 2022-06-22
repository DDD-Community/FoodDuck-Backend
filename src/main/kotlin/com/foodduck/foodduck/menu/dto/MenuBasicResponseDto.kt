package com.foodduck.foodduck.menu.dto

import io.swagger.annotations.ApiModelProperty

data class MenuBasicResponseDto(
    @ApiModelProperty(value = "메뉴 아이디", required = true)
    val menuId: Long?
)
