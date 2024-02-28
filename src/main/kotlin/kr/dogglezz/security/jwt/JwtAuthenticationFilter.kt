package kr.dogglezz.security.jwt

import com.fasterxml.jackson.databind.ObjectMapper
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import kr.dogglezz.security.LoginRequest
import kr.dogglezz.security.LoginResponse
import kr.dogglezz.security.member.Role
import org.slf4j.LoggerFactory
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.AuthenticationException
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter

class JwtAuthenticationFilter(
    private val jwtTokenProvider: JwtTokenProvider,
    private val objectMapper: ObjectMapper,
    private val authenticationManager: AuthenticationManager,
) : UsernamePasswordAuthenticationFilter() {

    private val log = LoggerFactory.getLogger(javaClass)

    override fun attemptAuthentication(request: HttpServletRequest, response: HttpServletResponse): Authentication {
        val value = objectMapper.readValue<LoginRequest>(request.inputStream, LoginRequest::class.java)
        val authentication =
            UsernamePasswordAuthenticationToken.unauthenticated(value.username, value.password)
        return authenticationManager.authenticate(authentication)
    }

    override fun successfulAuthentication(
        request: HttpServletRequest,
        response: HttpServletResponse,
        chain: FilterChain,
        authResult: Authentication,
    ) {
        val id = authResult.name
        val role = authResult.authorities.asSequence().map { it.authority }
            .find { Role.isSupport(it) } ?: return

        val accessToken = jwtTokenProvider.createToken(id.toLong(), role)
        val loginResponse = LoginResponse(accessToken)
        val value = objectMapper.writeValueAsString(loginResponse)
        response.writer.println(value)
    }

    override fun unsuccessfulAuthentication(
        request: HttpServletRequest,
        response: HttpServletResponse,
        failed: AuthenticationException,
    ) {
        log.error("failed authentication", failed)
        super.unsuccessfulAuthentication(request, response, failed)
    }
}