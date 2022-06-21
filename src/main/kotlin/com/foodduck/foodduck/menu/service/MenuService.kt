
package com.foodduck.foodduck.menu.service

import com.foodduck.foodduck.account.model.Account
import com.foodduck.foodduck.base.config.S3Uploader
import com.foodduck.foodduck.base.error.CustomException
import com.foodduck.foodduck.base.error.ErrorCode
import com.foodduck.foodduck.base.message.DEFAULT_TAG
import com.foodduck.foodduck.base.message.MENU_DIR_NAME
import com.foodduck.foodduck.base.message.MessageCode
import com.foodduck.foodduck.menu.dto.MenuBasicRequestDto
import com.foodduck.foodduck.menu.dto.MenuBasicResponseDto
import com.foodduck.foodduck.menu.dto.MenuIdsDto
import com.foodduck.foodduck.menu.dto.MenuModifyRequestDto
import com.foodduck.foodduck.menu.model.*
import com.foodduck.foodduck.menu.repository.*
import com.foodduck.foodduck.menu.vo.DetailMenuVIewVo
import com.foodduck.foodduck.menu.vo.FindFavorMenuListVo
import com.foodduck.foodduck.menu.vo.MenuAlbumListVo
import com.foodduck.foodduck.menu.vo.FindMenuListVo
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class MenuService(
    private val menuRepository: MenuRepository,
    private val tagRepository: TagRepository,
    private val tagMenuRepository: TagMenuRepository,
    private val favorMenuRepository: FavorMenuRepository,
    private val menuHistoryRepository: MenuHistoryRepository,
    private val s3Uploader: S3Uploader
) {

    @Transactional
    fun postMenu(account: Account, request: MenuBasicRequestDto): MenuBasicResponseDto {
        val rawTags = request.tags.plus(DEFAULT_TAG)
        generateTag(rawTags)
        val image = request.image
        val url = s3Uploader.upload(image, MENU_DIR_NAME)
        val rawMenu = request.toMenu(account, url)
        val menu = menuRepository.save(rawMenu)
        val tags = getTags(rawTags)
        tags?.forEach { tag -> tagMenuRepository.save(TagMenu(tag = tag, menu = menu)) }
        return MenuBasicResponseDto(menu.id)
    }

    private fun getTags(tags: List<String>): List<Tag>? {
        return tagRepository.findByTitleIn(tags)
    }

    private fun generateTag(tags: List<String>) {
        tags.filter { !tagRepository.existsByTitle(it) }.forEach { tagRepository.save(Tag(title=it)) }
    }

    @Transactional
    fun clickFavor(account: Account, menuId: Long): String {
        val menu: Menu = (menuRepository.findByIdAndDeleteIsFalse(menuId) ?: CustomException(ErrorCode.MENU_NOT_FOUND_ERROR)) as Menu
        val favorMenu = favorMenuRepository.findByAccountAndMenuAndDeleteIsFalse(account, menu)
        return if (favorMenu == null) {
            menu.changeFavorCount(1L)
            favorMenuRepository.save(FavorMenu(account = account, menu = menu))
            MessageCode.FAVOR_UP
        } else {
            menu.changeFavorCount(-1L)
            favorMenuRepository.delete(favorMenu)
            MessageCode.FAVOR_DOWN
        }
    }

    @Transactional
    fun detailMenu(account: Account?, menuId: Long): DetailMenuVIewVo {
        recordMenuHistory(account, menuId)
        return tagMenuRepository.detailMenuView(menuId) ?:throw CustomException(ErrorCode.MENU_NOT_FOUND_ERROR)
    }

    private fun recordMenuHistory(account: Account?, menuId: Long) {
        if (account != null) {
            val menu = menuRepository.findByIdAndDeleteIsFalse(menuId) ?: throw CustomException(ErrorCode.MENU_NOT_FOUND_ERROR)
            menuHistoryRepository.findByAccountAndMenuAndDeleteIsFalse(account, menu) ?: menuHistoryRepository.save(MenuHistory(menu = menu, account = account))
        }
    }

    fun listMenu(tagName: String, menuId: Long?, orderBy: String, pageSize: Long): List<FindMenuListVo> {
        return tagMenuRepository.findListMenu(tagName = tagName, lastId = menuId, orderBy = orderBy, pageSize = pageSize)
    }

    fun historyMenu(account: Account, menuId: Long?, pageSize: Long): List<MenuAlbumListVo> {
        return menuHistoryRepository.findMyMenuHistoryList(account, menuId, pageSize)
    }

    fun myFavorMenu(account: Account, menuId: Long?, pageSize: Long): List<FindFavorMenuListVo> {
        return favorMenuRepository.findFavorMenuList(account, menuId, pageSize)
    }

    fun myMenu(account: Account, menuId: Long?, pageSize: Long): List<MenuAlbumListVo> {
        return menuRepository.findMyMenuList(account, menuId, pageSize)
    }

    @Transactional
    fun modifyMenu(account: Account, menuId: Long, request: MenuModifyRequestDto): MenuBasicResponseDto {
        val menu = menuRepository.findByIdAndDeleteIsFalse(menuId) ?: CustomException(ErrorCode.MENU_NOT_FOUND_ERROR)
        val tagMenuList = tagMenuRepository.findByMenuAndDeleteIsFalse(menu as Menu)

        val requestTags = request.tags.plus(DEFAULT_TAG)
        val existsTags = tagMenuList.map { tagMenu -> tagMenu.tag.title }.plus(DEFAULT_TAG)
        val deleteTags = existsTags.subtract(requestTags).toList()

        deleteTagMenu(deleteTags, tagMenuList)
        makeTagAndTagMenu(requestTags, existsTags, menu)

        val image = request.image
        val url = s3Uploader.upload(image, MENU_DIR_NAME)
        menu.updateMenu(request.title, request.body, url)
        return MenuBasicResponseDto(menu.id)
    }

    private fun deleteTagMenu(deleteTags: List<String>?, tagMenuList: List<TagMenu>) {
        if (deleteTags != null) {
            tagMenuList.filter { deleteTags.contains(it.tag.title) }.forEach { tagMenu -> tagMenuRepository.delete(tagMenu) }
        }
    }

    private fun makeTagAndTagMenu(requestTags: Set<String>, existsTags: List<String>, menu: Menu) {
        val tags = requestTags.filter { !existsTags.contains(it) }.toList()
        for (title in tags) {
            val tag = tagRepository.findByTitle(title) ?: tagRepository.save(Tag(title = title))
            tagMenuRepository.save(TagMenu(menu = menu, tag = tag))
        }
    }

    @Transactional
    fun deleteMyMenuHistory(account: Account, request: MenuIdsDto) {
        val menuHistories = menuHistoryRepository.findByAccountAndMenu_IdInAndDeleteIsFalse(account, request.menuIds) ?: throw CustomException(ErrorCode.MENU_HISTORY_NOT_FOUND_ERROR)
        if (request.menuIds.size != menuHistories.size) {
            throw CustomException(ErrorCode.MENU_HISTORY_NOT_FOUND_ERROR)
        }
        menuHistories.forEach { it.remove() }
    }

    @Transactional
    fun deleteMyMenu(account: Account, menuId: Long) {
        val menu = menuRepository.findByIdAndDeleteIsFalseAndAccount(menuId, account) ?: throw CustomException(ErrorCode.MENU_NOT_FOUND_ERROR)
        tagMenuRepository.bulkDeleteTrue(menu)
        menuHistoryRepository.bulkDeleteTrue(menu)
        favorMenuRepository.bulkDeleteTrue(menu)
        menu.remove()
    }
}