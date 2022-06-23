package com.foodduck.foodduck.menu.service

import com.foodduck.foodduck.base.config.S3Uploader
import com.foodduck.foodduck.base.config.domain.EntityFactory
import com.foodduck.foodduck.base.error.CustomException
import com.foodduck.foodduck.base.error.ErrorCode
import com.foodduck.foodduck.base.message.DEFAULT_TAG
import com.foodduck.foodduck.base.message.MessageCode
import com.foodduck.foodduck.menu.dto.MenuBasicRequestDto
import com.foodduck.foodduck.menu.dto.MenuIdsDto
import com.foodduck.foodduck.menu.dto.MenuModifyRequestDto
import com.foodduck.foodduck.menu.model.*
import com.foodduck.foodduck.menu.repository.*
import com.foodduck.foodduck.menu.vo.DetailMenuVIewVo
import com.foodduck.foodduck.menu.vo.FindFavorMenuListVo
import com.foodduck.foodduck.menu.vo.MenuAlbumListVo
import com.foodduck.foodduck.menu.vo.FindMenuListVo
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import org.assertj.core.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import org.junit.jupiter.api.assertThrows
import org.springframework.mock.web.MockMultipartFile
import java.io.FileInputStream
import kotlin.streams.toList

internal class MenuServiceTest {
    private lateinit var menuService: MenuService

    @MockK
    private lateinit var menuRepository: MenuRepository

    @MockK
    private lateinit var tagRepository: TagRepository

    @MockK
    private lateinit var tagMenuRepository: TagMenuRepository

    @MockK
    private lateinit var favorMenuRepository: FavorMenuRepository

    @MockK
    private lateinit var menuHistoryRepository: MenuHistoryRepository

    @MockK
    private lateinit var s3Uploader: S3Uploader

    @BeforeEach
    fun setUp() {
        MockKAnnotations.init(this)
        menuService = MenuService(menuRepository, tagRepository, tagMenuRepository, favorMenuRepository, menuHistoryRepository, s3Uploader)
    }

    @Test
    fun `메뉴를 등록한다`() {
        val account = EntityFactory.accountTemplate()
        val tags = listOf<String>("KOREA")
        val fis = FileInputStream("src/test/resources/static/test.png")
        val image = MockMultipartFile("file", fis)
        val title = "동아시아 음식"
        val body = "정말 맛있어요"
        val request = MenuBasicRequestDto(image, title, body, tags)
        val dbTags = listOf(Tag(id=1L, title="KOREA"), Tag(id=2L, title=DEFAULT_TAG))
        every { tagRepository.existsByTitle("KOREA") } returns false
        every { tagRepository.existsByTitle(DEFAULT_TAG) } returns false
        every { tagRepository.save(any()) } returns Tag(title="any")
        every { tagRepository.findByTitleIn(any()) } returns dbTags
        every { menuRepository.save(any()) } returns Menu(title = title, body = body, account = account, url = image.name, id = 1L, favorCount = 0L)
        every { tagMenuRepository.save(any()) }.returnsArgument(0)
        every { s3Uploader.upload(any(), any()) }.returns("aws/s3/image.png")
        val result = menuService.postMenu(account, request)
        assertThat(result.menuId).isEqualTo(1)
    }

    @Test
    fun `메뉴를 등록할 때 태그가 이미 다 있으면 생성을 안한다`() {
        val account = EntityFactory.accountTemplate()
        val tags = listOf<String>("KOREA")
        val fis = FileInputStream("src/test/resources/static/test.png")
        val image = MockMultipartFile("file", fis)
        val title = "동아시아 음식"
        val body = "정말 맛있어요"
        val request = MenuBasicRequestDto(image, title, body, tags)
        val dbTags = listOf(Tag(id=1L, title="KOREA"), Tag(id=2L, title=DEFAULT_TAG))
        every { tagRepository.existsByTitle("KOREA") } returns true
        every { tagRepository.existsByTitle(DEFAULT_TAG) } returns true
        every { tagRepository.findByTitleIn(any()) } returns dbTags
        every { menuRepository.save(any()) } returns Menu(title = title, body = body, account = account, url = image.name, id = 1L, favorCount = 0L)
        every { tagMenuRepository.save(any()) }.returnsArgument(0)
        every { s3Uploader.upload(any(), any()) }.returns("aws/s3/image.png")
        val result = menuService.postMenu(account, request)
        assertThat(result.menuId).isEqualTo(1)
    }

    @Test
    fun `태그별로 필터링을 한다`() {
        val tagName = DEFAULT_TAG
        val orderBy = "-id"
        val pageSize = 3L
        val account = EntityFactory.accountTemplate()
        val account2 = EntityFactory.accountTemplate(2L, "TestDuck")
        val menuList = listOf<Menu>(
            EntityFactory.menuTemplate(account),
            EntityFactory.menuTemplate(account = account2, id = 2L),
            EntityFactory.menuTemplate(account = account2, id = 3L),
            EntityFactory.menuTemplate(account = account2, id = 4L)
        )
        val view: MutableList<FindMenuListVo> = mutableListOf()
        for (menu in menuList.reversed()) {
            menu.id?.let {
                FindMenuListVo(
                    menuId = it, nickname = menu.account.nickname,
                    url = menu.url, title = menu.title,
                    body = menu.body, count = menu.favorCount
                )
            }?.let { view.add(it) }
        }
        view.removeAt(3)
        assertThat(view.size).isEqualTo(3)
        every { tagMenuRepository.findListMenu(tagName = tagName, lastId = null, orderBy = orderBy, pageSize = pageSize) }.returns(view)
        val result = menuService.listMenu(tagName, null, orderBy, pageSize)
        assertThat(result).isEqualTo(view)
    }

    @Test
    fun `정렬을 id 오름차순`() {
        val tagName = DEFAULT_TAG
        val orderBy = "-id"
        val pageSize = 3L
        val account = EntityFactory.accountTemplate()
        val account2 = EntityFactory.accountTemplate(2L, "TestDuck")
        val menuList = listOf<Menu>(
            EntityFactory.menuTemplate(account),
            EntityFactory.menuTemplate(account = account2, id = 2L),
            EntityFactory.menuTemplate(account = account2, id = 3L),
            EntityFactory.menuTemplate(account = account2, id = 4L)
        )
        val view: MutableList<FindMenuListVo> = mutableListOf()
        for (menu in menuList) {
            menu.id?.let {
                FindMenuListVo(
                    menuId = it, nickname = menu.account.nickname,
                    url = menu.url, title = menu.title,
                    body = menu.body, count = menu.favorCount
                )
            }?.let { view.add(it) }
        }
        view.removeAt(3)
        assertThat(view.size).isEqualTo(3)
        every { tagMenuRepository.findListMenu(tagName = tagName, lastId = null, orderBy = orderBy, pageSize = pageSize) }.returns(view)
        val result = menuService.listMenu(tagName, null, orderBy, pageSize)
        assertThat(result).isEqualTo(view)
    }

    @Test
    fun `정렬을 favorCount 오름차순`() {
        val tagName = DEFAULT_TAG
        val orderBy = "favorCount"
        val pageSize = 3L
        val account = EntityFactory.accountTemplate()
        val account2 = EntityFactory.accountTemplate(2L, "TestDuck")
        val menuList = listOf<Menu>(
            EntityFactory.menuTemplate(account, favorCount = 3L),
            EntityFactory.menuTemplate(account = account2, id = 2L, favorCount = 10L),
            EntityFactory.menuTemplate(account = account2, id = 3L, favorCount = 15L),
            EntityFactory.menuTemplate(account = account2, id = 4L, favorCount = 4L)
        )
        val stream = menuList.stream()
        val view = stream.sorted { o1, o2 -> o2.favorCount.compareTo(o1.favorCount) }.map { menu ->
            menu.id?.let {
                FindMenuListVo(
                    menuId = it, nickname = menu.account.nickname,
                    url = menu.url, title = menu.title,
                    body = menu.body, count = menu.favorCount
                )
            }
        }.limit(3).toList()
        assertThat(view.size).isEqualTo(3)
        every { tagMenuRepository.findListMenu(tagName = tagName, lastId = null, orderBy = orderBy, pageSize = pageSize) }.returns(view)
        val result = menuService.listMenu(tagName, null, orderBy, pageSize)
        assertThat(result).isEqualTo(view)
        assertThat(result[0].count).isEqualTo(15L)
        assertThat(result[1].count).isEqualTo(10L)
        assertThat(result[2].count).isEqualTo(4L)
    }

    @Test
    fun `정렬을 favorCount 내림차순`() {
        val tagName = DEFAULT_TAG
        val orderBy = "-favorCount"
        val pageSize = 3L
        val account = EntityFactory.accountTemplate()
        val account2 = EntityFactory.accountTemplate(2L, "TestDuck")
        val menuList = listOf<Menu>(
            EntityFactory.menuTemplate(account, favorCount = 3L),
            EntityFactory.menuTemplate(account = account2, id = 2L, favorCount = 10L),
            EntityFactory.menuTemplate(account = account2, id = 3L, favorCount = 15L),
            EntityFactory.menuTemplate(account = account2, id = 4L, favorCount = 4L)
        )
        val stream = menuList.stream()
        val view = stream.sorted { o1, o2 -> o1.favorCount.compareTo(o2.favorCount) }.map { menu ->
            menu.id?.let {
                FindMenuListVo(
                    menuId = it, nickname = menu.account.nickname,
                    url = menu.url, title = menu.title,
                    body = menu.body, count = menu.favorCount
                )
            }
        }.limit(3).toList()
        assertThat(view.size).isEqualTo(3)
        every { tagMenuRepository.findListMenu(tagName = tagName, lastId = null, orderBy = orderBy, pageSize = pageSize) }.returns(view)
        val result = menuService.listMenu(tagName, null, orderBy, pageSize)
        assertThat(result[0].count).isEqualTo(3L)
        assertThat(result[1].count).isEqualTo(4L)
        assertThat(result[2].count).isEqualTo(10L)
    }

    @Test
    fun `좋아요 메뉴 리스트를 보여준다`() {
        val account = EntityFactory.accountTemplate()
        val account2 = EntityFactory.accountTemplate(id=2L, nickname = "another")
        val pageSize = 3L
        val lastId = 5L
        val menuList = listOf<Menu>(
            EntityFactory.menuTemplate(account2, favorCount = 3L),
            EntityFactory.menuTemplate(account = account2, id = 2L, favorCount = 10L),
            EntityFactory.menuTemplate(account = account2, id = 3L, favorCount = 15L),
            EntityFactory.menuTemplate(account = account2, id = 4L, favorCount = 4L),
            EntityFactory.menuTemplate(account = account2, id = 5L, favorCount = 4L),
            EntityFactory.menuTemplate(account = account2, id = 6L, favorCount = 4L),
            EntityFactory.menuTemplate(account = account2, id = 7L, favorCount = 4L)
        )
        val stream = menuList.stream()
        val view = stream
            .filter{it -> it.id!! < 5L}
            .sorted { o1, o2 -> o1.id?.let { o2.id?.compareTo(it) } ?: 0 }
            .map {
                it.id?.let {
                        it1 -> FindFavorMenuListVo(menuId = it1, nickname = it.account.nickname, url = it.url, title = it.title, body = it.body, count = it.favorCount )
                }
        }.limit(3).toList()
        every { favorMenuRepository.findFavorMenuList(account, lastId, pageSize) }.returns(view)
        val result = menuService.myFavorMenu(account, lastId, pageSize)
        assertThat(result.size).isEqualTo(3)
        assertThat(result).isEqualTo(view)
        assertThat(result[0].menuId).isEqualTo(4L)
    }

    @Test
    fun `내가 본 메뉴 기록을 보여준다`() {
        val account = EntityFactory.accountTemplate()
        val pageSize = 3L
        val menuList = listOf<Menu>(
            EntityFactory.menuTemplate(account, favorCount = 3L),
            EntityFactory.menuTemplate(account = account, id = 2L, favorCount = 10L),
            EntityFactory.menuTemplate(account = account, id = 3L, favorCount = 15L),
        ).reversed()
        val stream = menuList.stream()
        val view = stream.map { menu -> menu.id?.let { MenuAlbumListVo(menuId = it, url = menu.url) } }.toList()
        every { menuHistoryRepository.findMyMenuHistoryList(account, null, pageSize) }.returns(view)
        val result = menuService.historyMenu(account, null, pageSize)
        assertThat(result).isEqualTo(view)
    }

    @Test
    fun `좋아요를 누르면 좋아요 갯수와 좋아요 누른 사람의 기록이 쌓인다`() {
        val account = EntityFactory.accountTemplate()
        val menuId = 1L
        val menu = EntityFactory.menuTemplate(account)
        every { menuRepository.findByIdAndDeleteIsFalse(menuId) }.returns(menu)
        every { favorMenuRepository.findByAccountAndMenuAndDeleteIsFalse(account, menu) }.returns(null)
        every { favorMenuRepository.save(any()) }.returns(FavorMenu(account = account, menu = menu))
        val result = menuService.clickFavor(account, menuId)
        assertThat(result).isEqualTo(MessageCode.FAVOR_UP)
        assertThat(menu.favorCount).isEqualTo(1)
    }

    @Test
    fun `좋아요 취소를 누르면 좋아요 갯수가 감소되고 좋아요 메뉴에서 사라진다`() {
        val account = EntityFactory.accountTemplate()
        val menuId = 1L
        val menu = EntityFactory.menuTemplate(account, 1L)
        every { menuRepository.findByIdAndDeleteIsFalse(menuId) }.returns(menu)
        every { favorMenuRepository.findByAccountAndMenuAndDeleteIsFalse(account, menu) }.returns(FavorMenu(account = account, menu = menu))
        every { favorMenuRepository.delete(any()) }.returnsArgument(0)
        val result = menuService.clickFavor(account, menuId)
        assertThat(result).isEqualTo(MessageCode.FAVOR_DOWN)
        assertThat(menu.favorCount).isEqualTo(0)
    }

    @Test
    fun `좋아요를 누르는 순간 메뉴가 없을 때`() {
        val account = EntityFactory.accountTemplate()
        val menuId = 1L
        every { menuRepository.findByIdAndDeleteIsFalse(menuId) }throws CustomException(ErrorCode.MENU_NOT_FOUND_ERROR)
        assertThrows<CustomException> { menuService.clickFavor(account, menuId) }
    }

    @Test
    fun `로그인을 했을 때 메뉴를 누르면 메뉴 기록이 쌓인다`() {
        val account = EntityFactory.accountTemplate()
        val menuId = 1L
        val menu = EntityFactory.menuTemplate(account)
        val view = DetailMenuVIewVo(1L,menu.title, menu.body, menu.url, menu.favorCount, account.nickname)
        every { menuRepository.findByIdAndDeleteIsFalse(menuId) }.returns(menu)
        every { menuHistoryRepository.findMenuHistoryByAccountAndMenu(account, menu) }.returns(MenuHistory(menu = menu, account = account))
        every { tagMenuRepository.detailMenuView(menuId) }.returns(view)
        val result = menuService.detailMenu(account, menuId)
        assertThat(result).isEqualTo(view)
    }

    @Test
    fun `로그인을 했을 때 이미 봤던 기록은 MenuHistory 를 만들지 않는다`() {
        val account = EntityFactory.accountTemplate()
        val menuId = 1L
        val menu = EntityFactory.menuTemplate(account)
        val view = DetailMenuVIewVo(1L,menu.title, menu.body, menu.url, menu.favorCount, account.nickname)
        every { menuRepository.findByIdAndDeleteIsFalse(menuId) }.returns(menu)
        every { menuHistoryRepository.findMenuHistoryByAccountAndMenu(account, menu) }.returns(null)
        every { menuHistoryRepository.save(any()) }.returnsArgument(0)
        every { tagMenuRepository.detailMenuView(menuId) }.returns(view)
        val result = menuService.detailMenu(account, menuId)
        assertThat(result).isEqualTo(view)
    }

    @Test
    fun `로그인을 하지 않아도 상세 메뉴를 볼 수가 있다`() {
        val account = EntityFactory.accountTemplate()
        val menuId = 1L
        val menu = EntityFactory.menuTemplate(account)
        val view = DetailMenuVIewVo(1L,menu.title, menu.body, menu.url, menu.favorCount, account.nickname)
        every { tagMenuRepository.detailMenuView(menuId) }.returns(view)
        val result = menuService.detailMenu(null, menuId)
        assertThat(result).isEqualTo(view)
    }

    @Test
    fun `내가 적은 메뉴를 조회한다 이 때 다른 사람의 메뉴는 보이지 않아야 한다`() {
        val account = EntityFactory.accountTemplate()
        val other = EntityFactory.accountTemplate(id = 2L, nickname = "other")
        val menuId = 1L
        val menu = EntityFactory.menuTemplate(account = account)
        EntityFactory.menuTemplate(account = other, id = 2L)
        val view = listOf<MenuAlbumListVo>(MenuAlbumListVo(menuId = menuId, url = menu.url))
        every { menuRepository.findMyMenuList(account, menuId, 3L) }.returns(view)
        val result = menuService.myMenu(account, menuId, 3L)
        assertThat(result.size).isEqualTo(1)
        assertThat(result).isEqualTo(view)
    }

    @Test
    fun `메뉴를 수정한다 다른 태그가 왔을 때에는 이전 태그 삭제하고 추가한다`() {
        val account = EntityFactory.accountTemplate()
        val menuId = 1L
        val menu = EntityFactory.menuTemplate(account)
        val fis = FileInputStream("src/test/resources/static/test.png")
        val image = MockMultipartFile("file", fis)
        val request = MenuModifyRequestDto(image = image, title = menu.title, body = menu.body, tags = setOf("중식"))
        every { menuRepository.findByIdAndDeleteIsFalse(menuId) } returns menu
        every { tagMenuRepository.findByMenuAndDeleteIsFalse(menu) } returns listOf(TagMenu(menu = menu, tag = Tag(title = "한식")))
        every { tagMenuRepository.delete(any()) } .returnsArgument(0)
        every { tagRepository.findByTitle(any()) }.returns(null)
        every { tagRepository.save(any()) }.returns(Tag(title = "중식"))
        every { s3Uploader.upload(any(), any()) }.returns("aws/s3/image.png")
        every { tagMenuRepository.save(any()) }.returnsArgument(0)
        val result = menuService.modifyMenu(account, menuId, request)
        assertThat(result.menuId).isEqualTo(menu.id)
    }

    @Test
    fun `메뉴를 수정하는데 메뉴가 존재하지 않을 때`() {
        val account = EntityFactory.accountTemplate()
        val menuId = 1L
        val fis = FileInputStream("src/test/resources/static/test.png")
        val image = MockMultipartFile("file", fis)
        val menu = EntityFactory.menuTemplate(account)
        val request = MenuModifyRequestDto(image = image, title = menu.title, body = menu.body, tags = setOf("중식"))
        every { menuRepository.findByIdAndDeleteIsFalse(menuId) } throws CustomException(ErrorCode.MENU_NOT_FOUND_ERROR)
        every { s3Uploader.upload(any(), any()) }.returns("aws/s3/image.png")
        assertThrows<CustomException> { menuService.modifyMenu(account, menuId, request) }
    }

    @Test
    fun `메뉴 기록을 삭제한다`() {
        val account = EntityFactory.accountTemplate()
        val menuList = listOf<Menu>(
            EntityFactory.menuTemplate(account = account),
            EntityFactory.menuTemplate(id = 2L, account = account),
            EntityFactory.menuTemplate(id = 3L, account = account),
            EntityFactory.menuTemplate(id = 4L, account = account),
            EntityFactory.menuTemplate(id = 5L, account = account)
        )
        val menuIds = menuList.map { it.id }.toList()
        val menuHistories = menuList.map { MenuHistory(menu = it, account = account) }.toList()
        every { menuHistoryRepository.findMenuHistoryListByAccountAndMenuIds(account, menuIds as List<Long>) }.returns(menuHistories)
        assertDoesNotThrow {
            menuService.deleteMyMenuHistory(account, MenuIdsDto(menuIds = menuIds as List<Long>))
            val count = menuHistories.count { it.delete }
            assertThat(count).isEqualTo(5)
        }
    }

    @Test
    fun `메뉴 기록을 이미 삭제한 아이디를 다시 할려고 할 때 요청 갯수와 다를 때`() {
        val account = EntityFactory.accountTemplate()
        val menuList = listOf<Menu>(
            EntityFactory.menuTemplate(account = account),
            EntityFactory.menuTemplate(id = 2L, account = account),
            EntityFactory.menuTemplate(id = 3L, account = account),
            EntityFactory.menuTemplate(id = 4L, account = account),
            EntityFactory.menuTemplate(id = 5L, account = account)
        )
        menuList.last().remove()
        val menuIds = menuList.map { it.id }.toList()
        val menuHistories = menuList.filter { !it.delete }.map { MenuHistory(menu = it, account = account) }.toList()
        every { menuHistoryRepository.findMenuHistoryListByAccountAndMenuIds(account, menuIds as List<Long>) }.returns(menuHistories)
        assertThrows<CustomException> {
            menuService.deleteMyMenuHistory(account, MenuIdsDto(menuIds = menuIds as List<Long>))
        }
    }

    @Test
    fun `메뉴 기록을 이미 삭제한 아이디를 다시 할려고 할 때 아예 존재하지 않을 때`() {
        val account = EntityFactory.accountTemplate()
        val menuList = listOf<Menu>(
            EntityFactory.menuTemplate(account = account),
            EntityFactory.menuTemplate(id = 2L, account = account),
            EntityFactory.menuTemplate(id = 3L, account = account),
            EntityFactory.menuTemplate(id = 4L, account = account),
            EntityFactory.menuTemplate(id = 5L, account = account)
        )
        for (menu in menuList) {
            menu.remove()
        }
        val menuIds = menuList.map { it.id }.toList()
        every { menuHistoryRepository.findMenuHistoryListByAccountAndMenuIds(account, menuIds as List<Long>) } throws CustomException(ErrorCode.MENU_HISTORY_NOT_FOUND_ERROR)
        assertThrows<CustomException> {
            menuService.deleteMyMenuHistory(account, MenuIdsDto(menuIds = menuIds as List<Long>))
        }
    }

    @Test
    fun `메뉴를 삭제한다`() {
        val account = EntityFactory.accountTemplate()
        val menu = EntityFactory.menuTemplate(account)
        val tag = Tag(title= DEFAULT_TAG)
        val tagMenu = TagMenu(tag = tag, menu = menu)
        val menuHistory = MenuHistory(account = account, menu = menu)
        val favorMenu = FavorMenu(menu = menu, account = account)
        every { menu.id?.let { menuRepository.findByIdAndDeleteIsFalseAndAccount(it, account) } } returns menu
        every { tagMenuRepository.bulkDeleteTrue(menu) }.apply { tagMenu.remove() }.returns(1)
        every { menuHistoryRepository.bulkDeleteTrue(menu) }.apply { menuHistory.remove() }.returns(1)
        every { favorMenuRepository.bulkDeleteTrue(menu) }.apply { favorMenu.remove() }.returns(1)
        assertDoesNotThrow {
            menu.id?.let { menuService.deleteMyMenu(account, it) }
            assertThat(tagMenu.delete).isTrue
            assertThat(menuHistory.delete).isTrue
            assertThat(favorMenu.delete).isTrue
            assertThat(menu.delete).isTrue
        }
    }
}