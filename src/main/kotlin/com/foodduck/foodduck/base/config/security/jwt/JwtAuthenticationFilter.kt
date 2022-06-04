package com.foodduck.foodduck.base.config.security.jwt

import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.filter.OncePerRequestFilter
import javax.servlet.FilterChain
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

class JwtAuthenticationFilter(private val jwtProvider: JwtProvider): OncePerRequestFilter() {
    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        val accessToken: String? = jwtProvider.getAccessTokenFromHeader(request)
        if (accessToken?.let { jwtProvider.validateToken(request, it) } == true) {
            setAuthentication(accessToken)
        }
        filterChain.doFilter(request, response)
    }

    private fun setAuthentication(token: String) {
        val authentication: Authentication = jwtProvider.getAuthentication(token)
        SecurityContextHolder.getContext().authentication = authentication
    }
}