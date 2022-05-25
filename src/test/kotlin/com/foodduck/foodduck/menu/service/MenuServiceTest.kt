package com.foodduck.foodduck.menu.service

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

internal class MenuServiceTest {

    @Test
    fun `메뉴를 선택한다`() {
        // 로그인 한 유저의 경우 history 정보를 redis 에 저장 최댓 개수 고려
        // 로그인 하지 않은 경우에는 다음 스탭으로 진행
        // 메뉴 관련 조합 재료 관련 정보를 뿌려준다.
    }

    @Test
    fun `좋아요를 누른다`() {
        // Favor 의 값을 증가시킨다.
        // FavoriteMenu 리스트를 추가한다.
    }

    @Test
    fun `좋아요를 취소한다`() {
        // Favor 의 값을 감소시킨다.
        // FavoriteMenu 리스트를 삭제시킨다.
    }

    @Test
    fun `메뉴 눌렀던 히스토리 정보를 보여준다`() {
        // redis 조회를 통해 뿌려준다.
    }

    @Test
    fun `메뉴를 등록한다`() {
        // 카테고리, 아이템 정보를 선택
    }
}