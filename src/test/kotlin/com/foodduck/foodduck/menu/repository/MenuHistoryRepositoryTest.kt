package com.foodduck.foodduck.menu.repository

import com.foodduck.foodduck.account.model.Account
import com.foodduck.foodduck.account.repository.AccountRepository
import com.foodduck.foodduck.base.config.TestConfig
import com.foodduck.foodduck.base.config.domain.EntityFactory
import com.foodduck.foodduck.menu.model.Menu
import com.foodduck.foodduck.menu.model.MenuHistory
import org.assertj.core.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.context.annotation.Import
import org.springframework.data.repository.findByIdOrNull
import org.springframework.transaction.annotation.Transactional
import javax.persistence.EntityManager
import javax.persistence.PersistenceContext
import kotlin.streams.toList

@DataJpaTest
@Import(TestConfig::class)
@Transactional
class MenuHistoryRepositoryTest {

    @PersistenceContext
    lateinit var entityManager: EntityManager

    @Autowired
    lateinit var menuHistoryRepository: MenuHistoryRepository

    @Autowired
    lateinit var menuRepository: MenuRepository

    @Autowired
    lateinit var accountRepository: AccountRepository

    private lateinit var accounts: List<Account>

    private lateinit var menus: List<Menu>

    @BeforeEach
    fun setUp() {
        accounts = listOf<Account>(
            EntityFactory.accountTemplateForReal(),
            EntityFactory.accountTemplateForReal(nickname = "testDuck"),
            EntityFactory.accountTemplateForReal(nickname = "Foodtest"),
            EntityFactory.accountTemplateForReal(nickname = "myDuck")
        )

        menus = accounts.map { EntityFactory.menuTemplateForReal(account = it) }.toList()

        accountRepository.saveAll(accounts)
        menuRepository.saveAll(menus)
    }

    @Test
    fun `메뉴 히스토리 유저 정보와 메뉴 아이디로 조회`() {
        val account = accounts.first()
        val menu = menus.first()
        val menuHistory = menuHistoryRepository.save(MenuHistory(menu = menu, account = account))
        val findMenuHistory = menuHistoryRepository.findMenuHistoryByAccountAndMenu(account, menu)
        assertThat(findMenuHistory).isEqualTo(menuHistory)
    }

    @Test
    fun `메뉴 히스토리 조회하는데 메뉴가 delete가 되었을 때`() {
        val account = accounts.first()
        val menu = menus.first()
        menu.remove()
        menuHistoryRepository.save(MenuHistory(menu = menu, account = account))
        val findMenuHistory = menuHistoryRepository.findMenuHistoryByAccountAndMenu(account, menu)
        assertThat(findMenuHistory).isNull()
    }
    
    @Test
    fun `메뉴 히스토리 조회하는데 메뉴 히스토리가 delete가 되었을 때`() {
        val account = accounts.first()
        val menu = menus.first()
        val menuHistory = menuHistoryRepository.save(MenuHistory(menu = menu, account = account))
        menuHistory.remove()
        val findMenuHistory = menuHistoryRepository.findMenuHistoryByAccountAndMenu(account, menu)
        assertThat(findMenuHistory).isNull()
    }
    
    @Test
    fun `메뉴 히스토리 메뉴 아이디 리스트로 조회할 때`() {
        val account = accounts.first()
        menus.map { menuHistoryRepository.save(MenuHistory(account = account, menu = it)) }.toList()
        val menuIds = menus.map { it.id }.toList()
        val findMenuHistories = menuHistoryRepository.findMenuHistoryListByAccountAndMenuIds(account, menuIds as List<Long>)
        assertThat(findMenuHistories!!.size).isEqualTo(menus.size)
        assertThat(findMenuHistories.last().menu.id).isEqualTo(menuIds.last())
    }

    @Test
    fun `메뉴 히스토리 메뉴 아이디 리스트로 조회하는데 다 delete true 일 때`() {
        val account = accounts.first()
        val menuHistories = menus.map { menuHistoryRepository.save(MenuHistory(account = account, menu = it)) }.toList()
        val checkAccount = accounts.last()
        menus.map { menuHistoryRepository.save(MenuHistory(account = checkAccount, menu = it)) }.toList()
        for (menuHistory in menuHistories) {
            menuHistory.remove()
        }
        val menuIds = menus.map { it.id }.toList()
        val findMenuHistories = menuHistoryRepository.findMenuHistoryListByAccountAndMenuIds(account, menuIds as List<Long>)
        assertThat(findMenuHistories).isEmpty()
    }

    @Test
    fun `메뉴 히스토리 delete 참`() {
        val menu = menus.first()
        val menuHistories = accounts.map { menuHistoryRepository.save(MenuHistory(account = it, menu = menu)) }.toList()
        val size = menuHistoryRepository.bulkDeleteTrue(menu)
        assertThat(size).isEqualTo(menus.size)
        entityManager.flush()
        entityManager.clear()
        for (menuHistory in menuHistories) {
            val findMenuHistory = menuHistoryRepository.findByIdOrNull(menuHistory.id)
            assertThat(findMenuHistory!!.delete).isTrue
        }
    }

    @Test
    fun `메뉴 히스토리 페이징 마지막 아이디 넘겨주지 않을 때`() {
        val account = accounts.first()
        menus.map { menuHistoryRepository.save(MenuHistory(account = account, menu = it)) }.toList()
        val menuIds = menus.sortedByDescending { menu -> menu.id }.map { it.id }.toList()
        val result = menuHistoryRepository.findMyMenuHistoryList(account, null, 2)
        assertThat(result.size).isEqualTo(2)
        val firstResult = result[0]
        assertThat(firstResult.menuId).isEqualTo(menuIds[0])
    }

    @Test
    fun `메뉴 히스토리 페이징 마지막 아이디 넘겨줄 때`() {
        val account = accounts.first()
        menus.map { menuHistoryRepository.save(MenuHistory(account = account, menu = it)) }.toList()
        val menuIds = menus.sortedByDescending { menu -> menu.id }.map { it.id }.toList()
        val stream = menuIds.stream()
        val skipMenuIds = stream.skip(2).toList()
        val result = menuHistoryRepository.findMyMenuHistoryList(account, menuIds[1], 2)
        assertThat(result.size).isEqualTo(2)
        assertThat(result[0].menuId).isEqualTo(skipMenuIds[0])
    }

    @Test
    fun `메뉴 히스토리 페이징 끝 아이디 넘길 때 빈 값`() {
        val account = accounts.first()
        menus.map { menuHistoryRepository.save(MenuHistory(account = account, menu = it)) }.toList()
        val menuIds = menus.sortedByDescending { menu -> menu.id }.map { it.id }.toList()
        val result = menuHistoryRepository.findMyMenuHistoryList(account, menuIds.last(), 2)
        assertThat(result).isEmpty()
    }
}