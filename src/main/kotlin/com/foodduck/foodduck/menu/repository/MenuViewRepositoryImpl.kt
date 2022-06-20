package com.foodduck.foodduck.menu.repository

import com.foodduck.foodduck.account.model.Account
import com.foodduck.foodduck.menu.model.QMenu.menu
import com.foodduck.foodduck.menu.vo.MenuAlbumListVo
import com.querydsl.core.types.Projections
import com.querydsl.core.types.dsl.BooleanExpression
import com.querydsl.jpa.impl.JPAQueryFactory

class MenuViewRepositoryImpl(
    private val query: JPAQueryFactory
):MenuViewRepository {

    override fun findMyMenuList(account: Account, menuId: Long?, pageSize: Long): List<MenuAlbumListVo> {
        return query
            .select(Projections.constructor(MenuAlbumListVo::class.java, menu.id, menu.url))
            .from(menu)
            .where(menu.delete.isFalse, ltMenuId(menuId))
            .limit(pageSize)
            .orderBy(menu.id.desc())
            .fetch()

    }

    private fun ltMenuId(menuId: Long?): BooleanExpression? {
        if (menuId == null)
            return null
        return menu.id.lt(menuId)
    }
}