
package com.foodduck.foodduck.menu.dto

import io.swagger.annotations.ApiModel
import io.swagger.annotations.ApiModelProperty
import org.springframework.web.multipart.MultipartFile

@ApiModel(description = "메뉴 수정 요청")
data class MenuModifyRequestDto(
    @ApiModelProperty(value = "이미지 file", required = true)
    val image:MultipartFile,
    @ApiModelProperty(value = "메뉴 제목", required = true)
    val title: String,
    @ApiModelProperty(value = "메뉴 내용", required = true)
    val body: String,
    @ApiModelProperty(value = "태그들", required = true)
    val tags: Set<String>
)