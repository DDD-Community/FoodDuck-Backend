package com.foodduck.foodduck.menu.repository

import com.foodduck.foodduck.account.model.QAccount.account
import com.foodduck.foodduck.menu.model.QMenu.menu
import com.foodduck.foodduck.menu.model.QTag.tag
import com.foodduck.foodduck.menu.model.QTagMenu.tagMenu
import com.foodduck.foodduck.menu.vo.DetailMenuVIewVo
import com.foodduck.foodduck.menu.vo.FindMenuListVo
import com.querydsl.core.types.Order
import com.querydsl.core.types.OrderSpecifier
import com.querydsl.core.types.Projections
import com.querydsl.core.types.dsl.BooleanExpression
import com.querydsl.jpa.impl.JPAQuery
import com.querydsl.jpa.impl.JPAQueryFactory

class TagMenuViewRepositoryImpl(
    private val query: JPAQueryFactory
):TagMenuViewRepository {

    override fun findListMenu(tagName: String, lastId: Long?, orderBy:String, pageSize: Long): List<FindMenuListVo> {
        val query = query.select(
            Projections.constructor(
                FindMenuListVo::class.java,
                tagMenu.menu.id, tagMenu.menu.account.nickname, tagMenu.menu.url, tagMenu.menu.title, tagMenu.menu.body, tagMenu.menu.favorCount
            )
        ).from(tagMenu)
            .join(tagMenu.menu, menu)
            .join(tagMenu.tag, tag)
            .join(tagMenu.menu.account, account)
            .where(tagMenuDeleteIsFalse(), menuDeleteIsFalse(), tagEq(tagName), ltLastMenuId(lastId))
            .limit(pageSize)

        val resQuery = orderByQuery(query, orderBy)
        return resQuery!!.fetch()
    }

    override fun detailMenuView(menuId: Long): DetailMenuVIewVo? {
        return query.select(
            Projections.constructor(DetailMenuVIewVo::class.java,
                tagMenu.menu.id, tagMenu.menu.title,
                tagMenu.menu.body, tagMenu.menu.url,
                tagMenu.menu.favorCount,
                tagMenu.menu.account.nickname
            )
        ).from(tagMenu)
            .where(tagMenuDeleteIsFalse(), menuDeleteIsFalse(), tagMenu.menu.id.eq(menuId))
            .distinct()
            .fetchOne()
    }

    override fun findTagsByMenu(menuId: Long): List<String> {
        return query.select(tagMenu.tag.title)
            .from(tagMenu)
            .where(tagMenuDeleteIsFalse(), menuDeleteIsFalse(), tagMenu.menu.id.eq(menuId))
            .fetch()
    }

    private fun menuDeleteIsFalse(): BooleanExpression? {
        return tagMenu.menu.delete.isFalse
    }

    private fun tagMenuDeleteIsFalse(): BooleanExpression? {
        return tagMenu.delete.isFalse
    }

    private fun tagEq(tagName: String): BooleanExpression? {
        return tagMenu.tag.title.eq(tagName)
    }

    private fun ltLastMenuId(menuId: Long?): BooleanExpression? {
        if (menuId == null)
            return null
        return tagMenu.menu.id.lt(menuId)
    }

    private fun orderByQuery(query: JPAQuery<FindMenuListVo>?, orderBy: String): JPAQuery<FindMenuListVo>? {
        val orders = orderBy.split(",")
        var resQuery = query
        for (order in orders) {
            var direction: Order = Order.ASC
            if (order.startsWith("-")) {
                direction = Order.DESC
                resQuery = addOrderByQuerySet(order.substring(1), resQuery, direction)
            }
            else {
                resQuery = addOrderByQuerySet(order, resQuery, direction)
            }
        }
        return resQuery
    }

    private fun addOrderByQuerySet(order: String, resQuery: JPAQuery<FindMenuListVo>?, direction: Order): JPAQuery<FindMenuListVo>? {
        var result = resQuery
        when (order) {
            "favorCount" -> {
                result = result?.orderBy(OrderSpecifier(direction, tagMenu.menu.favorCount))
            }
            "id" -> {
                result = result?.orderBy(OrderSpecifier(direction, tagMenu.id))
            }
        }
        return result
    }
}