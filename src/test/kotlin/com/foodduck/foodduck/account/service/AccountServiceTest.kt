package com.foodduck.foodduck.account.service

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

internal class AccountServiceTest {

    @Test
    fun `회원가입을 시도한다`() {
        // 닉네임이 unique 한 지 확인한다.
        // 비밀번호 regex 하게 했는 지 확인한다.
        // 이메일을 보낸다.

        // 번호를 입력한다.
        // 회원가입에 성공한다.

        // 응답으로 accessToken, refreshToken 값을 준다.
    }

    @Test
    fun `로그인 시도`() {
        // 이메일과 비밀번호 입력
        // 일치여부 확인
        // 응답으로 accessToken, refreshToken 값을 준다.
    }

    @Test
    fun `본인 인증 비밀번호 확인`() {
        // 요청값과 redis 값을 비교한다.
        // 일치하면 200 OK, 그렇지 않으면 400
    }

    @Test
    fun `accessToken 이 만료되었을 때 refreshToken 값을 주면 다시 accessToken 과 refreshToken 을 준다`() {
        // 클라이언트에서 refreshToken 값을 준다.
        // 서버에서 refreshToken 의 Subject 값을 가지고 redis 를 조회한다.
        // 값이 존재하지 않으면 에러
        // 존재하면 토큰 재발급
        // redis 정보 업데이트
        // 응답으로 accessToken, refreshToken 값 발행
    }

    @Test
    fun `비밀번호 찾기`() {
        // email 요청값 확인
        // 존재하지 않으면 에러
        // 있으면 랜덤 비밀번호 생성 및 변경
        // 사용자가 해당 비밀번호로 로그인
    }

    @Test
    fun `비밀번호 변경`() {
        // 현재 비밀번호
        // 바꿀 비밀번호
        // 바꿀 비밀번호 regex 한 지 확인
        // 응답으로 200 OK
    }

    @Test
    fun `내 정보 조회`() {
        // 응답으로 닉네임, 이메일 반환
    }

    @Test
    fun `회원 탈퇴`() {
        // is_delete true 로 설정
        // 실제로는 삭제 안함
    }

}