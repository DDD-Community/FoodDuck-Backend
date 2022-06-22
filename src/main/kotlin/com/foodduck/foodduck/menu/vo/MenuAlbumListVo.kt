
package com.foodduck.foodduck.menu.vo

import io.swagger.annotations.ApiModelProperty

data class MenuAlbumListVo(
    @ApiModelProperty(value = "메뉴 아이디", required = true)
    val menuId: Long,
    @ApiModelProperty(value = "메뉴 이미지 url", required = true)
    val url: String
)