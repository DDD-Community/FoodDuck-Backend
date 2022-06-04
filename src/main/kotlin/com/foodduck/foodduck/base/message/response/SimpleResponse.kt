package com.foodduck.foodduck.base.message.response

import org.springframework.http.HttpStatus

class SimpleResponse<T>(
    val status:Int,
    val message:String?,
    val data: T?
) {
    private constructor(status: HttpStatus, message: String?) : this(status.value(), message, null)

    companion object {
        fun <T> of(status: HttpStatus, message: String?, data: T?): SimpleResponse<T> {
            return SimpleResponse(status.value(), message, data)
        }

        fun of(status: HttpStatus, message: String?): SimpleResponse<Unit> {
            return SimpleResponse(status, message)
        }
    }
}