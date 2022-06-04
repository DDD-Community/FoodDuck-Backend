package com.foodduck.foodduck.account.model

import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.User

class UserAccount(val account: Account) :
    User(account.email, account.password, listOf(SimpleGrantedAuthority("ROLE_USER"))) {
}