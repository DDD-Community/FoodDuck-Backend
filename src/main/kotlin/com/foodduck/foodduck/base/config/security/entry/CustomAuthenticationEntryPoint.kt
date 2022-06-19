package com.foodduck.foodduck.base.config.security.entry

import org.springframework.http.HttpStatus
import org.springframework.security.core.AuthenticationException
import org.springframework.security.web.AuthenticationEntryPoint
import org.springframework.stereotype.Component
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@Component
class CustomAuthenticationEntryPoint:AuthenticationEntryPoint {

    override fun commence(
        request: HttpServletRequest?,
        response: HttpServletResponse?,
        authException: AuthenticationException?
    ) {
        setResponse(response)
        response?.sendError(HttpServletResponse.SC_UNAUTHORIZED)
    }

    private fun setResponse(response: HttpServletResponse?) {
        response?.status = HttpStatus.FORBIDDEN.value()
        response?.contentType = "application/json"
        response?.characterEncoding = "UTF-8"
    }
}