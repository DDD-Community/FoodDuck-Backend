package com.foodduck.foodduck.menu.vo

import io.swagger.annotations.ApiModelProperty
import java.time.LocalDateTime

data class FindMenuListVo(
    @ApiModelProperty(value = "메뉴 아이디", required = true)
    val menuId: Long,
    @ApiModelProperty(value = "메뉴 작성자 이름", required = true)
    val nickname: String,
    @ApiModelProperty(value = "메뉴 이미지 url", required = true)
    val url: String,
    @ApiModelProperty(value = "메뉴 제목", required = true)
    val title: String,
    @ApiModelProperty(value = "메뉴 내용", required = true)
    val body: String,
    @ApiModelProperty(value = "메뉴 좋아요 갯수", required = true)
    val count: Long,
    @ApiModelProperty(value = "생성날짜", required = true)
    val createdAt: LocalDateTime
)
