package com.foodduck.foodduck.menu.vo

import io.swagger.annotations.ApiModelProperty

data class DetailMenuVIewVo(
    @ApiModelProperty(value = "메뉴 아이디", required = true)
    val id: Long,
    @ApiModelProperty(value = "메뉴 제목", required = true)
    val title: String,
    @ApiModelProperty(value = "메뉴 내용", required = true)
    val body: String,
    @ApiModelProperty(value = "메뉴 이미지 url", required = true)
    val url: String,
    @ApiModelProperty(value = "메뉴 좋아요 갯수", required = true)
    val favorCount: Long,
    @ApiModelProperty(value = "작성자 이름", required = true)
    val nickname: String
)
