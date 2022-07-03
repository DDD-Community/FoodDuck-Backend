
package com.foodduck.foodduck.menu.vo

import io.swagger.annotations.ApiModelProperty
import java.time.LocalDateTime

data class MenuAlbumListVo(
    @ApiModelProperty(value = "메뉴 아이디", required = true)
    val menuId: Long,
    @ApiModelProperty(value = "메뉴 이미지 url", required = true)
    val url: String,
    @ApiModelProperty(value = "생성날짜", required = true)
    val createdAt: LocalDateTime
)