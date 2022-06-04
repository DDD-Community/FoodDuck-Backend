package com.foodduck.foodduck.base.config.security

import org.springframework.security.test.context.support.WithSecurityContext

@Retention(AnnotationRetention.RUNTIME)
@WithSecurityContext(factory = WithUserDetailsSecurityContextFactory::class)
annotation class FoodDuckTestUser
