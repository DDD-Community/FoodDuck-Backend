package com.foodduck.foodduck.account.repository

import com.foodduck.foodduck.account.model.Account
import com.foodduck.foodduck.account.vo.FindMyInfo

interface AccountViewRepository {
    fun findMyInfo(account: Account): FindMyInfo?
}