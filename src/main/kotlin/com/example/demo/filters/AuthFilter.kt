package com.example.demo.filters

import com.example.demo.Constants
import io.jsonwebtoken.Jwts
import org.springframework.core.Ordered
import org.springframework.core.annotation.Order
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Component
import org.springframework.web.filter.GenericFilterBean
import java.io.IOException
import javax.servlet.*
import javax.servlet.http.HttpFilter
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@Component
class AuthFilter : Filter {
    @Throws(IOException::class, ServletException::class)
    override fun doFilter(servletRequest: ServletRequest, servletResponse: ServletResponse, filterChain: FilterChain) {

        val httpRequest = servletRequest as HttpServletRequest
        val httpResponse = servletResponse as HttpServletResponse
        val authHeader = httpRequest.getHeader("Authorization")
        if (authHeader != null) {
            val authHeaderArr: Array<String?> = authHeader.split("Bearer ").toTypedArray()
            if (authHeaderArr.size > 1 && authHeaderArr[1] != null) {
                val token = authHeaderArr[1]
                try {
                    val claims = Jwts.parser().setSigningKey(Constants.API_SECRET_KEY)
                            .parseClaimsJws(token).body
                    httpRequest.setAttribute("userId", claims["userId"].toString().toInt())
                } catch (e: Exception) {
                    httpResponse.sendError(HttpStatus.FORBIDDEN.value(), "invalid/expired token")
                    return
                }
            } else {
                httpResponse.sendError(HttpStatus.FORBIDDEN.value(), "Authorization token must be Bearer [token]")
                return
            }
        } else {
            httpResponse.sendError(HttpStatus.FORBIDDEN.value(), "Authorization token must be provided")
            return
        }
        filterChain.doFilter(servletRequest, servletResponse)
    }
}