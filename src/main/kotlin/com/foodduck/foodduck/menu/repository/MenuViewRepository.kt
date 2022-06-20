package com.foodduck.foodduck.menu.repository

import com.foodduck.foodduck.account.model.Account
import com.foodduck.foodduck.menu.vo.MenuAlbumListVo

interface MenuViewRepository {
    fun findMyMenuList(account: Account, menuId: Long?, pageSize: Long): List<MenuAlbumListVo>
}