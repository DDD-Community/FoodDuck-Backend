package com.foodduck.foodduck.menu.dto

import org.springframework.web.multipart.MultipartFile

data class MenuModifyRequestDto(
    val image:MultipartFile,
    val title: String,
    val body: String,
    val tags: Set<String>
) {
}