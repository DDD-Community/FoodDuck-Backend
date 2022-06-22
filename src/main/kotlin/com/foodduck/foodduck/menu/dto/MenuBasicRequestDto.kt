
package com.foodduck.foodduck.menu.dto

import com.foodduck.foodduck.account.model.Account
import com.foodduck.foodduck.menu.model.Menu
import io.swagger.annotations.ApiModel
import io.swagger.annotations.ApiModelProperty
import org.springframework.web.multipart.MultipartFile

data class MenuBasicRequestDto(
    @ApiModelProperty(value = "이미지 file", required = true)
    val image:MultipartFile,
    @ApiModelProperty(value = "글 제목", required = true)
    val title: String,
    @ApiModelProperty(value = "글 내용", required = true)
    val body: String,
    @ApiModelProperty(value = "태그들", required = true)
    val tags: List<String>
) {
    fun toMenu(account: Account, url: String): Menu {
        return Menu(title = title, body = body, account = account, favorCount = 0L, url=url)
    }
}