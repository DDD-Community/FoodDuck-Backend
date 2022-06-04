package com.foodduck.foodduck.base.error

class ErrorResponse(
    val status: Int,
    val code: String,
    val message: String
) {
    companion object {
        fun of(errorCode: ErrorCode): ErrorResponse {
            return ErrorResponse(errorCode.status.value(), errorCode.code, errorCode.message)
        }
    }

}