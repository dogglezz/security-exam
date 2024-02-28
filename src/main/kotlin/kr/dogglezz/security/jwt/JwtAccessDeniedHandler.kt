package kr.dogglezz.security.jwt

import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.slf4j.LoggerFactory
import org.springframework.security.access.AccessDeniedException
import org.springframework.security.web.access.AccessDeniedHandler

class JwtAccessDeniedHandler : AccessDeniedHandler {

    private val log = LoggerFactory.getLogger(javaClass)

    override fun handle(
        request: HttpServletRequest,
        response: HttpServletResponse,
        accessDeniedException: AccessDeniedException?,
    ) {
        log.error("JwtAccessDeniedHandler ex", accessDeniedException)
        response.sendError(HttpServletResponse.SC_FORBIDDEN)
    }
}