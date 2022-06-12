package com.foodduck.foodduck.menu.dto

import org.springframework.web.multipart.MultipartFile

data class MenuCreateRequest(
    val image:MultipartFile,
    val title: String,
    val body: String,
    val tags: List<String>
) {
}