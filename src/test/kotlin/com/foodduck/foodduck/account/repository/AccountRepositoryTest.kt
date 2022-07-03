package com.foodduck.foodduck.account.repository

import com.foodduck.foodduck.account.model.Account
import com.foodduck.foodduck.base.config.TestConfig
import com.foodduck.foodduck.base.config.domain.EntityFactory
import com.foodduck.foodduck.menu.repository.MenuRepository
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

    @Autowired
    lateinit var menuRepository: MenuRepository

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

    @Test
    fun `내 정보 조회 좋아요 받은 갯수 및 게시물 쓴 갯수`() {
        val account = EntityFactory.accountTemplateForReal()
        val menus = listOf(
            EntityFactory.menuTemplateForReal(account = account, favorCount = 1L),
            EntityFactory.menuTemplateForReal(account = account, favorCount = 2L),
            EntityFactory.menuTemplateForReal(account = account, favorCount = 3L),
            EntityFactory.menuTemplateForReal(account = account, favorCount = 0L),
            EntityFactory.menuTemplateForReal(account = account, favorCount = 2L)
        )
        menus.last().remove()
        val findAccount = accountRepository.save(account)
        val findMenus = menus.map { menuRepository.save(it) }.toList()
        val findMyInfo = accountRepository.findMyInfo(account)
        assertThat(findMyInfo).isNotNull
        assertThat(findMyInfo!!.nickname).isEqualTo(findAccount.nickname)
        assertThat(findMyInfo.myFavorCount).isEqualTo(6L)
        assertThat(findMyInfo.myMenuCount).isEqualTo(4L)
    }

}