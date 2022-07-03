package com.foodduck.foodduck.account.repository

import com.foodduck.foodduck.account.model.Account
import org.springframework.data.jpa.repository.JpaRepository

interface AccountRepository: JpaRepository<Account, Long>, AccountViewRepository {
    fun findByEmail(email: String): Account?
    fun existsByNickname(nickname: String): Boolean
    fun existsByEmail(email: String): Boolean
}