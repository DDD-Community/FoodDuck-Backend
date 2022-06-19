package com.foodduck.foodduck.base.message

class MessageCode {
    companion object {
        const val OK = "성공하셨습니다."
        const val SELECT_OK = "조회에 성공하셨습니다."
        const val SIGN_UP_SUCCESS = "회원가입에 성공하셨습니다."
        const val LOGIN_SUCCESS = "로그인에 성공하셨습니다."
        const val LOGOUT_SUCCESS = "로그아웃에 성공하셨습니다."
        const val SEND_SUCCESS = "전송하였습니다."
        const val SIGN_OUT = "회원탈퇴 하셨습니다."
        const val MENU_CREATE = "메뉴 생성 성공하셨습니다."
        const val FAVOR_UP = "좋아요가 증가했습니다"
        const val FAVOR_DOWN = "좋아요가 감소했습니다"
    }
}