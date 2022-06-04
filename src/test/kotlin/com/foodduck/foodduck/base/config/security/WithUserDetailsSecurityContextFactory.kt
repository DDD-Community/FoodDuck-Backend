package com.foodduck.foodduck.base.config.security

import com.foodduck.foodduck.account.model.UserAccount
import com.foodduck.foodduck.base.config.domain.EntityFactory
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContext
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.test.context.support.WithSecurityContextFactory

internal class WithUserDetailsSecurityContextFactory : WithSecurityContextFactory<FoodDuckTestUser> {
    override fun createSecurityContext(annotation: FoodDuckTestUser?): SecurityContext {
        val principal : UserDetails = UserAccount(EntityFactory.accountTemplate())
        val authentication = UsernamePasswordAuthenticationToken(principal, null, principal.authorities)
        val context = SecurityContextHolder.createEmptyContext()
        context.authentication = authentication
        return context
    }

}