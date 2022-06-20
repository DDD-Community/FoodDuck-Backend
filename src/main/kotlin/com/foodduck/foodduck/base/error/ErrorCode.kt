package com.foodduck.foodduck.base.error

import org.springframework.http.HttpStatus

enum class ErrorCode(
    val status: HttpStatus,
    val code: String,
    val message: String
) {
    JWT_BLACK_LIST_ERROR(HttpStatus.BAD_REQUEST, "A001", "탈취 위험이 있는 토큰입니다."),
    JWT_WRONG_FORMAT_ERROR(HttpStatus.BAD_REQUEST, "A002", "잘못된 JWT 형식입니다."),
    USER_NOT_FOUND_ERROR(HttpStatus.BAD_REQUEST, "A003", "유저를 찾을 수 없습니다."),
    TOKEN_PARSER_ERROR(HttpStatus.BAD_REQUEST, "A004", "토큰 검사하는 도중 에러가 발생했습니다."),
    ALREADY_EXISTS_NICKNAME_ERROR(HttpStatus.BAD_REQUEST, "A005", "이미 존재하고 있는 닉네임입니다."),
    NOT_EQUAL_AUTHENTICATE_NUMBER_ERROR(HttpStatus.BAD_REQUEST, "A006", "저장된 인증번호와 일치하지 않습니다."),
    NOT_EQUAL_PASSWORD_ERROR(HttpStatus.BAD_REQUEST, "A007", "1차 비밀번호와 2처 비밀번호가 일치하지 않습니다."),
    EMAIL_FORMAT_ERROR(HttpStatus.BAD_REQUEST, "A008", "이메일 형식이 아닙니다."),
    ALREADY_EXISTS_EMAIL_ERROR(HttpStatus.BAD_REQUEST, "A009", "이미 존재하고 있는 이메일입니다."),
    PASSWORD_FORMAT_ERROR(HttpStatus.BAD_REQUEST, "A010", "비밀번호 형식이 아닙니다."),
    WRONG_PASSWORD_ERROR(HttpStatus.BAD_REQUEST, "A011", "잘못된 비밀번호 입니다."),
    MENU_NOT_FOUND_ERROR(HttpStatus.BAD_REQUEST, "B001", "존재하지 않는 메뉴 입니다."),
    MENU_HISTORY_NOT_FOUND_ERROR(HttpStatus.BAD_REQUEST, "B002", "존재하지 않는 메뉴 기록 입니다."),
    FILE_NOT_FOUND(HttpStatus.NOT_FOUND, "C001", "파일을 찾을 수 없습니다.")
}