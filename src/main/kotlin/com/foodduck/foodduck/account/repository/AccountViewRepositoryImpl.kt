package com.foodduck.foodduck.account.repository

import com.foodduck.foodduck.account.model.Account
import com.foodduck.foodduck.account.model.QAccount
import com.foodduck.foodduck.account.vo.FindMyInfo
import com.foodduck.foodduck.menu.model.QMenu.menu
import com.querydsl.core.types.Projections
import com.querydsl.jpa.impl.JPAQueryFactory

class AccountViewRepositoryImpl(private val query: JPAQueryFactory): AccountViewRepository {
    override fun findMyInfo(account: Account): FindMyInfo? {
        return query.select(
            Projections.constructor(FindMyInfo::class.java, menu.account.nickname, menu.favorCount.sum(), menu.count())
        ).from(menu)
            .join(menu.account, QAccount.account)
            .where(menu.account.eq(account), menu.delete.isFalse)
            .fetchOne()
    }
}