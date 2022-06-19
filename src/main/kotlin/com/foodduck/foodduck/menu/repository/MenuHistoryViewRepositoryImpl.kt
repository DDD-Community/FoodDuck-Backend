package com.foodduck.foodduck.menu.repository

import com.foodduck.foodduck.account.model.Account
import com.foodduck.foodduck.menu.model.QMenu
import com.foodduck.foodduck.menu.model.QMenuHistory.menuHistory
import com.foodduck.foodduck.menu.vo.FindMenuHistoryListVo
import com.querydsl.core.types.Projections
import com.querydsl.core.types.dsl.BooleanExpression
import com.querydsl.jpa.impl.JPAQueryFactory

class MenuHistoryViewRepositoryImpl(
    private val query: JPAQueryFactory
):MenuHistoryViewRepository {

    override fun findMyMenuHistoryList(account: Account, menuId: Long?, pageSize:Long): List<FindMenuHistoryListVo> {
        return query
            .select(
                Projections.constructor(FindMenuHistoryListVo::class.java, menuHistory.menu.id, menuHistory.menu.url)
            ).from(menuHistory)
            .join(menuHistory.menu, QMenu.menu)
            .where(menuHistory.menu.delete.isFalse, accountEq(account), ltLastMenuId(menuId))
            .limit(pageSize)
            .orderBy(menuHistory.id.desc())
            .fetch()
    }

    private fun accountEq(account: Account): BooleanExpression? {
        return menuHistory.account.eq(account)
    }

    private fun ltLastMenuId(menuId: Long?): BooleanExpression? {
        if (menuId == null)
            return null
        return menuHistory.menu.id.lt(menuId)
    }
}