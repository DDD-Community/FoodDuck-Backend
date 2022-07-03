
package com.foodduck.foodduck.menu.repository

import com.foodduck.foodduck.account.model.Account
import com.foodduck.foodduck.account.model.QAccount

import com.foodduck.foodduck.menu.model.QFavorMenu.favorMenu
import com.foodduck.foodduck.menu.model.QMenu.menu
import com.foodduck.foodduck.menu.vo.FindFavorMenuListVo
import com.querydsl.core.types.Projections
import com.querydsl.core.types.dsl.BooleanExpression
import com.querydsl.jpa.impl.JPAQueryFactory

class FavorMenuViewRepositoryImpl(
    private val query: JPAQueryFactory
): FavorMenuViewRepository {
    override fun findFavorMenuList(account: Account, lastId: Long?, pageSize: Long): List<FindFavorMenuListVo> {
        return query
            .select(
                Projections.constructor(
                    FindFavorMenuListVo::class.java,
                    favorMenu.menu.id, favorMenu.account.nickname, favorMenu.menu.url,
                    favorMenu.menu.title, favorMenu.menu.body, favorMenu.menu.favorCount,
                    favorMenu.menu.createdAt
                )
            ).from(favorMenu)
            .join(favorMenu.menu, menu)
            .join(favorMenu.account, QAccount.account)
            .where(favorMenu.delete.isFalse, favorMenu.menu.delete.isFalse, ltLastMenuId(lastId))
            .limit(pageSize)
            .orderBy(favorMenu.id.desc())
            .fetch()
    }

    private fun ltLastMenuId(lastId: Long?): BooleanExpression? {
        if (lastId == null) {
            return null
        }
        return favorMenu.menu.id.lt(lastId)
    }
}