package com.foodduck.foodduck.base.error

class CustomException(
    val errorCode: ErrorCode
): RuntimeException() {
}