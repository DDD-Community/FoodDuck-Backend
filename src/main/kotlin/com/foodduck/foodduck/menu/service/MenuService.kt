package com.foodduck.foodduck.menu.service

import com.foodduck.foodduck.account.model.Account
import com.foodduck.foodduck.base.error.CustomException
import com.foodduck.foodduck.base.error.ErrorCode
import com.foodduck.foodduck.base.message.MessageCode
import com.foodduck.foodduck.menu.dto.MenuCreateRequestDto
import com.foodduck.foodduck.menu.dto.MenuCreateResponseDto
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
) {

    @Transactional
    fun postMenu(account: Account, request: MenuCreateRequestDto): MenuCreateResponseDto {
        // image s3 upload and get url
        val rawTags = request.tags.plus("INIT_ALL")
        generateTag(rawTags)
        val rawMenu = request.toMenu(account)
        val menu = menuRepository.save(rawMenu)
        val tags = getTags(rawTags)
        tags?.forEach { tag -> tagMenuRepository.save(TagMenu(tag = tag, menu = menu)) }
        return MenuCreateResponseDto(menu.id)
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
        val favorMenu = favorMenuRepository.findByAccountAndMenu(account, menu)
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
            menuHistoryRepository.findByAccountAndMenu(account, menu) ?: menuHistoryRepository.save(MenuHistory(menu = menu, account = account))
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