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
import com.foodduck.foodduck.menu.vo.FindMenuHistoryListVo
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
        tags.filter { !tagRepository.existsByTitle(it) }.forEach{ tagRepository.save(Tag(title=it)) }
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
        return tagMenuRepository.detailMenuView(menuId)?:throw CustomException(ErrorCode.MENU_NOT_FOUND_ERROR)
    }

    private fun recordMenuHistory(account: Account?, menuId: Long) {
        if (account != null) {
            val menu = menuRepository.findByIdAndDeleteIsFalse(menuId) ?: throw CustomException(ErrorCode.MENU_NOT_FOUND_ERROR)
            menuHistoryRepository.findByAccountAndMenuAndDeleteIsFalse(account, menu) ?: menuHistoryRepository.save(MenuHistory(menu = menu, account = account))
        }
    }

    fun listMenu(tagName: String, menuId:Long?, orderBy:String, pageSize: Long): List<FindMenuListVo> {
        return tagMenuRepository.findListMenu(tagName = tagName, lastId = menuId, orderBy = orderBy, pageSize = pageSize)
    }

    fun historyMenu(account: Account, menuId:Long?, pageSize: Long): List<FindMenuHistoryListVo> {
        return menuHistoryRepository.findMyMenuHistoryList(account, menuId, pageSize)
    }

    fun myFavorMenu(account: Account, menuId:Long?, pageSize: Long): List<FindFavorMenuListVo> {
        return favorMenuRepository.findFavorMenuList(account, menuId, pageSize)
    }

}