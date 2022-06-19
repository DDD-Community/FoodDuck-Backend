package com.foodduck.foodduck.base.config.security

import com.foodduck.foodduck.base.config.security.entry.CustomAuthenticationEntryPoint
import com.foodduck.foodduck.base.config.security.jwt.JwtAuthenticationFilter
import com.foodduck.foodduck.base.config.security.jwt.JwtProvider
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter

@Configuration
class WebSecurityConfig(
    private val jwtProvider: JwtProvider,
    private val customAuthenticationEntryPoint: CustomAuthenticationEntryPoint
) {

    @Bean
    fun configure(http: HttpSecurity?): SecurityFilterChain? {
        return http?.csrf()?.disable()?.exceptionHandling()?.authenticationEntryPoint(customAuthenticationEntryPoint)?.and()
            ?.sessionManagement()?.sessionCreationPolicy(SessionCreationPolicy.STATELESS)?.and()?.authorizeRequests()
            ?.antMatchers(
                "/api/v1/accounts/**",
                "/api/v1/tag-menus/**",
                "/api/v1/menus/{\\d+}"
            )?.permitAll()
            ?.anyRequest()?.authenticated()?.and()
            ?.addFilterBefore(JwtAuthenticationFilter(jwtProvider), UsernamePasswordAuthenticationFilter::class.java)
            ?.build()
    }
}

