package com.foodduck.foodduck.menu.repository

import com.foodduck.foodduck.account.model.Account
import com.foodduck.foodduck.menu.vo.FindFavorMenuListVo

interface FavorMenuViewRepository {
    fun findFavorMenuList(account: Account, lastId: Long?, pageSize:Long):List<FindFavorMenuListVo>
}