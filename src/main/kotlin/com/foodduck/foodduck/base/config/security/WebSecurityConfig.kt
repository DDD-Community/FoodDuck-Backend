package com.foodduck.foodduck.base.config.security

import com.foodduck.foodduck.base.config.security.entry.CustomAuthenticationEntryPoint
import com.foodduck.foodduck.base.config.security.jwt.JwtAuthenticationFilter
import com.foodduck.foodduck.base.config.security.jwt.JwtProvider
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter

@Configuration
class WebSecurityConfig(
    private val jwtProvider: JwtProvider,
    private val customAuthenticationEntryPoint: CustomAuthenticationEntryPoint
) {

    @Bean
    fun webConfigure() = WebSecurityCustomizer { web -> web.ignoring().antMatchers("swagger-ui/index.html", "swagger*/**") }


    @Bean
    fun configure(http: HttpSecurity?): SecurityFilterChain? {
        return http?.csrf()?.disable()?.exceptionHandling()?.authenticationEntryPoint(customAuthenticationEntryPoint)?.and()
            ?.sessionManagement()?.sessionCreationPolicy(SessionCreationPolicy.STATELESS)?.and()?.authorizeRequests()
            ?.antMatchers(
                "/api/v1/accounts/**",
                "/api/v1/tag-menus/**",
                "/api/v1/menus/{\\d+}",
                "/swagger*/**",
                "/v2/api-docs*/**",
                "/api/v1/comments/**"
            )?.permitAll()
            ?.anyRequest()?.authenticated()?.and()
            ?.addFilterBefore(JwtAuthenticationFilter(jwtProvider), UsernamePasswordAuthenticationFilter::class.java)
            ?.build()
    }
}

