package com.foodduck.foodduck.account.repository

import com.foodduck.foodduck.account.model.Account
import com.foodduck.foodduck.base.config.TestConfig
import com.foodduck.foodduck.base.config.domain.EntityFactory
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.context.annotation.Import
import org.springframework.transaction.annotation.Transactional

@DataJpaTest
@Import(TestConfig::class)
@Transactional
class AccountRepositoryTest {
    @Autowired
    lateinit var accountRepository: AccountRepository

    private lateinit var account:Account

    @BeforeEach
    fun setUp() {
        account = EntityFactory.accountTemplateForReal()
        accountRepository.save(account)
    }

    @Test
    fun `이메일 조회 성공`() {
        assertDoesNotThrow {
            accountRepository.findByEmail(account.email) ?: throw RuntimeException()
        }
    }

    @Test
    fun `이메일 조회 실패`() {
        assertThrows<RuntimeException> {
            accountRepository.findByEmail("wrong@example.com") ?: throw RuntimeException()
        }
    }

    @Test
    fun `닉네임 존재 여부 참`() {
        val result = accountRepository.existsByNickname(account.nickname)
        assertThat(result).isTrue
    }

    @Test
    fun `닉네임 존재 여부 거짓`() {
        val result = accountRepository.existsByNickname("hello-world")
        assertThat(result).isFalse
    }

    @Test
    fun `이메일 존재 여부 참`() {
        val result = accountRepository.existsByEmail(account.email)
        assertThat(result).isTrue
    }

    @Test
    fun `이메일 존재 여부 거짓`() {
        val result = accountRepository.existsByEmail("wrong@example.com")
        assertThat(result).isFalse
    }

}