
package com.foodduck.foodduck.menu.repository

import com.foodduck.foodduck.menu.vo.DetailMenuVIewVo
import com.foodduck.foodduck.menu.vo.FindMenuListVo

interface TagMenuViewRepository {
    fun findListMenu(tagName: String, lastId: Long?, orderBy: String, pageSize: Long): List<FindMenuListVo>

    fun detailMenuView(menuId: Long): DetailMenuVIewVo?

    fun findTagsByMenu(menuId: Long): List<String>
}