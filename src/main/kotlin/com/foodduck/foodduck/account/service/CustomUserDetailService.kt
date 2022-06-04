package com.foodduck.foodduck.account.service

import com.foodduck.foodduck.account.model.Account
import com.foodduck.foodduck.account.model.UserAccount
import com.foodduck.foodduck.account.repository.AccountRepository
import com.foodduck.foodduck.base.error.CustomException
import com.foodduck.foodduck.base.error.ErrorCode
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.stereotype.Service

@Service
class CustomUserDetailService: UserDetailsService{
    @Autowired
    private lateinit var accountRepository: AccountRepository

    override fun loadUserByUsername(email: String?): UserDetails {
        val account: Account = email?.let { accountRepository.findByEmail(it) } ?: throw CustomException(ErrorCode.USER_NOT_FOUND_ERROR)
        return UserAccount(account)
    }

}