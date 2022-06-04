package com.foodduck.foodduck.base.util

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

internal class RegexFoodDuckUtilTest {

    @Test
    fun `이메일 정규식 확인`() {
        val email = "tom@example.com"
        assertThat(RegexUtil.validEmail(email)).isTrue
    }

    @Test
    fun `이메일 정규식 마지막 dot 미포함`() {
        val email = "tom@example"
        assertThat(RegexUtil.validEmail(email)).isFalse
    }

    @Test
    fun `이메일 정규식 숫자 포함 확인`() {
        val email = "tom123@exmaple.com"
        assertThat(RegexUtil.validEmail(email)).isTrue
    }

    @Test
    fun `이메일 정규식 특수기호 포함 확인`() {
        val email = "tom!##@example.com"
        assertThat(RegexUtil.validEmail(email)).isTrue
    }

    @Test
    fun `이메일 정규식 숫자 + 특수기호 확인`() {
        val email = "tom12!#@example.com"
        assertThat(RegexUtil.validEmail(email)).isTrue
    }

    @Test
    fun `이메일 dot 포함 확인`() {
        val email = "tom.amily.dev@example.com"
        assertThat(RegexUtil.validEmail(email)).isTrue
    }

    @Test
    fun `비밀번호 정규식 정상`() {
        val password = "Test123!"
        assertThat(RegexUtil.validPassword(password)).isTrue
    }

    @Test
    fun `비밀번호 특수기호 빠짐`() {
        val password = "Test1234"
        assertThat(RegexUtil.validPassword(password)).isFalse
    }

    @Test
    fun `비밀번호 특수기호 !포함`() {
        val password = "Test123!"
        assertThat(RegexUtil.validPassword(password)).isTrue
    }

    @Test
    fun `비밀번호 특수기호 @포함`() {
        val password = "Test123@"
        assertThat(RegexUtil.validPassword(password)).isTrue
    }

    @Test
    fun `비밀번호 특수기호 #포함`() {
        val password = "Test123#"
        assertThat(RegexUtil.validPassword(password)).isTrue
    }

    @Test
    fun `비밀번호 특수기호 $포함`() {
        val password = "Test123$"
        assertThat(RegexUtil.validPassword(password)).isTrue
    }

    @Test
    fun `비밀번호 특수기호 %포함`() {
        val password = "Test123%"
        assertThat(RegexUtil.validPassword(password)).isTrue
    }

    @Test
    fun `비밀번호 특수기호 ^포함`() {
        val password = "Test123^"
        assertThat(RegexUtil.validPassword(password)).isTrue
    }

    @Test
    fun `비밀번호 특수기호 &포함`() {
        val password = "Test123&"
        assertThat(RegexUtil.validPassword(password)).isTrue
    }

    @Test
    fun `비밀번호 특수기호 *포함`() {
        val password = "Test123*"
        assertThat(RegexUtil.validPassword(password)).isTrue
    }

    @Test
    fun `비밀번호 특수기호 (포함`() {
        val password = "Test123("
        assertThat(RegexUtil.validPassword(password)).isTrue
    }

    @Test
    fun `비밀번호 특수기호 )포함`() {
        val password = "Test123)"
        assertThat(RegexUtil.validPassword(password)).isTrue
    }

    @Test
    fun `비밀번호 특수기호 -포함`() {
        val password = "Test123-"
        assertThat(RegexUtil.validPassword(password)).isTrue
    }

    @Test
    fun `비밀번호 특수기호 _포함`() {
        val password = "Test123_"
        assertThat(RegexUtil.validPassword(password)).isTrue
    }

    @Test
    fun `비밀번호 특수기호 =포함`() {
        val password = "Test123="
        assertThat(RegexUtil.validPassword(password)).isTrue
    }

    @Test
    fun `비밀번호 특수기호 +포함`() {
        val password = "Test123+"
        assertThat(RegexUtil.validPassword(password)).isTrue
    }

    @Test
    fun `비밀번호 특수기호 |포함`() {
        val password = "Test123|"
        assertThat(RegexUtil.validPassword(password)).isTrue
    }

    @Test
    fun `비밀번호 특수기호 닫힌 대괄호 포함`() {
        val password = "Test123]"
        assertThat(RegexUtil.validPassword(password)).isTrue
    }

    @Test
    fun `비밀번호 특수기호 닫힌 중괄호 포함`() {
        val password = "Test123}"
        assertThat(RegexUtil.validPassword(password)).isTrue
    }

    @Test
    fun `비밀번호 특수기호 열린 대괄호 포함`() {
        val password = "Test123["
        assertThat(RegexUtil.validPassword(password)).isTrue
    }

    @Test
    fun `비밀번호 특수기호 열린 중괄호 포함`() {
        val password = "Test123{"
        assertThat(RegexUtil.validPassword(password)).isTrue
    }

    @Test
    fun `비밀번호 특수기호 dot 포함`() {
        val password = "Test123."
        assertThat(RegexUtil.validPassword(password)).isTrue
    }

    @Test
    fun `비밀번호 특수기호 뾰족한 괄호1 포함`() {
        val password = "Test123>"
        assertThat(RegexUtil.validPassword(password)).isTrue
    }

    @Test
    fun `비밀번호 특수기호 뾰족한 괄호2 포함`() {
        val password = "Test123<"
        assertThat(RegexUtil.validPassword(password)).isTrue
    }

    @Test
    fun `비밀번호 특수기호 쉼표 포함`() {
        val password = "Test123,"
        assertThat(RegexUtil.validPassword(password)).isTrue
    }

    @Test
    fun `비밀번호 특수기호 semi colon 포함`() {
        val password = "Test123;"
        assertThat(RegexUtil.validPassword(password)).isTrue
    }

    @Test
    fun `비밀번호 특수기호 colon 포함`() {
        val password = "Test123:"
        assertThat(RegexUtil.validPassword(password)).isTrue
    }

    @Test
    fun `비밀번호 특수기호 홑 따옴표 포함`() {
        val password = "Test123'"
        assertThat(RegexUtil.validPassword(password)).isTrue
    }

    @Test
    fun `비밀번호 특수기호 쌍 따옴표 포함`() {
        val password = "Test123\""
        assertThat(RegexUtil.validPassword(password)).isTrue
    }

    @Test
    fun `비밀번호 글자수 확인 limit 20`() {
        val password = "Test123\$56789Test123"
        assertThat(RegexUtil.validPassword(password)).isTrue
    }

    @Test
    fun `비밀번호 글자수 초과 확인`() {
        val password = "Test123\$56789Test123LimitOver"
        assertThat(RegexUtil.validPassword(password)).isFalse
    }

}