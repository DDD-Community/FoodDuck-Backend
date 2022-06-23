package com.foodduck.foodduck.menu.repository

import com.foodduck.foodduck.account.model.Account
import com.foodduck.foodduck.account.repository.AccountRepository
import com.foodduck.foodduck.base.config.TestConfig
import com.foodduck.foodduck.base.config.domain.EntityFactory
import com.foodduck.foodduck.menu.model.FavorMenu
import com.foodduck.foodduck.menu.model.Menu
import org.assertj.core.api.Assertions.assertThat
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
class FavorMenuRepositoryTest {

    @PersistenceContext
    lateinit var entityManager: EntityManager

    @Autowired
    lateinit var favorMenuRepository: FavorMenuRepository

    @Autowired
    lateinit var accountRepository: AccountRepository

    @Autowired
    lateinit var menuRepository: MenuRepository

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
    fun `유저와 메뉴 정보로 유저가 좋아했던 메뉴 조회`() {
        val firstAccount = accounts.first()
        val firstMenu = menus.first()
        val favorMenu = favorMenuRepository.save(FavorMenu(account = firstAccount, menu = firstMenu))
        val findFavorMenu = favorMenuRepository.findByAccountAndMenuAndDeleteIsFalse(account = firstAccount, menu = firstMenu)
        assertThat(findFavorMenu).isNotNull
        assertThat(findFavorMenu).isEqualTo(favorMenu)
    }

    @Test
    fun `유저와 메뉴 조회 그런데 없을 경우`() {
        val firstAccount = accounts.first()
        val firstMenu = menus.first()
        val lastMenu = menus.last()
        favorMenuRepository.save(FavorMenu(account = firstAccount, menu = lastMenu))
        val findFavorMenu = favorMenuRepository.findByAccountAndMenuAndDeleteIsFalse(account = firstAccount, menu = firstMenu)
        assertThat(findFavorMenu).isNull()
    }

    @Test
    fun `좋아요 메뉴 있지만 delete 처리가 된 경우`() {
        val firstAccount = accounts.first()
        val firstMenu = menus.first()
        val favorMenu = favorMenuRepository.save(FavorMenu(account = firstAccount, menu = firstMenu))
        favorMenu.remove()
        val findFavorMenu = favorMenuRepository.findByAccountAndMenuAndDeleteIsFalse(account = firstAccount, menu = firstMenu)
        assertThat(findFavorMenu).isNull()
    }

    @Test
    fun `전체 삭제`() {
        val menu = menus.first()
        val favorMenus = accounts.map { favorMenuRepository.save(FavorMenu(account = it, menu = menu)) }.toList()
        val isRemove = favorMenuRepository.bulkDeleteTrue(menu)
        entityManager.flush()
        entityManager.clear()
        assertThat(isRemove).isEqualTo(4)
        for (favorMenu in favorMenus) {
            val findFavorMenu: FavorMenu? = favorMenu.id?.let { favorMenuRepository.findByIdOrNull(it) }
            assertThat(findFavorMenu!!.delete).isTrue
        }
    }

    @Test
    fun `좋아요 리스트 마지막 아이디 주지 않을 때`() {
        val account = accounts.first()
        val orderedMenus = menus.sortedByDescending { menu -> menu.id }
        menus.map { favorMenuRepository.save(FavorMenu(menu = it, account = account)) }.toList()
        val result = favorMenuRepository.findFavorMenuList(account = account, lastId = null, pageSize = 2)
        assertThat(result.size).isEqualTo(2)
        val firstResult = result.first()
        assertThat(firstResult.menuId).isEqualTo(orderedMenus.first().id)
        assertThat(firstResult.nickname).isEqualTo(account.nickname)
        assertThat(firstResult.url).isEqualTo(orderedMenus.first().url)
        assertThat(firstResult.title).isEqualTo(orderedMenus.first().title)
        assertThat(firstResult.body).isEqualTo(orderedMenus.first().body)
        assertThat(result.last().menuId).isEqualTo(orderedMenus[1].id)
    }

    @Test
    fun `좋아요 리스트 마지막 아이디 넘겨 줄 때`() {
        val account = accounts.first()
        val orderedMenus = menus.sortedByDescending { menu -> menu.id }
        menus.map { favorMenuRepository.save(FavorMenu(menu = it, account = account)) }.toList()
        val result = favorMenuRepository.findFavorMenuList(account = account, lastId = orderedMenus[1].id, pageSize = 2)
        val stream = orderedMenus.stream()
        val expectedMenus = stream.skip(2).toList()
        assertThat(result.size).isEqualTo(2)
        val firstResult = result.first()
        assertThat(firstResult.menuId).isEqualTo(expectedMenus.first().id)
        assertThat(firstResult.nickname).isEqualTo(account.nickname)
        assertThat(firstResult.url).isEqualTo(expectedMenus.first().url)
        assertThat(firstResult.title).isEqualTo(expectedMenus.first().title)
        assertThat(firstResult.body).isEqualTo(expectedMenus.first().body)
        assertThat(result.last().menuId).isEqualTo(expectedMenus[1].id)
    }

    @Test
    fun `좋아요 리스트 마지막 페이지일 때`() {
        val account = accounts.first()
        val orderedMenus = menus.sortedByDescending { menu -> menu.id }
        menus.map { favorMenuRepository.save(FavorMenu(menu = it, account = account)) }.toList()
        val result = favorMenuRepository.findFavorMenuList(account = account, lastId = orderedMenus[3].id, pageSize = 2)
        assertThat(result.size).isEqualTo(0)
    }

    @Test
    fun `좋아요 리스트 다 delete true가 될 때 zero`() {
        val account = accounts.first()
        menus.sortedByDescending { menu -> menu.id }
        val favorMenus = menus.map { favorMenuRepository.save(FavorMenu(menu = it, account = account)) }.toList()
        for (favorMenu in favorMenus) {
            favorMenu.remove()
        }
        val result = favorMenuRepository.findFavorMenuList(account = account, lastId = null, pageSize = 2)
        assertThat(result.size).isZero
    }
}
