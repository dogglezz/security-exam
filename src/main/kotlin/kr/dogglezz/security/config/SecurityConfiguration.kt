package kr.dogglezz.security.config

import com.fasterxml.jackson.databind.ObjectMapper
import kr.dogglezz.security.jwt.JwtAccessDeniedHandler
import kr.dogglezz.security.jwt.JwtAuthenticationEntryPoint
import kr.dogglezz.security.jwt.JwtAuthenticationFilter
import kr.dogglezz.security.jwt.JwtTokenProvider
import org.springframework.boot.autoconfigure.security.servlet.PathRequest
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.AuthenticationEntryPoint
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.access.AccessDeniedHandler
import org.springframework.security.web.authentication.AuthenticationEntryPointFailureHandler
import org.springframework.security.web.authentication.AuthenticationFailureHandler
import org.springframework.security.web.util.matcher.AntPathRequestMatcher

@Configuration
@EnableWebSecurity
class SecurityConfiguration(
    private val jwtTokenProvider: JwtTokenProvider,
    private val objectMapper: ObjectMapper,
    private val authenticationConfiguration: AuthenticationConfiguration,
) {

    @Bean
    fun securityFilterChain(http: HttpSecurity): SecurityFilterChain {
        return http
            .csrf { it.disable() }

            .exceptionHandling {
                it
                    .accessDeniedHandler(jwtAccessDeniedHandler())
                    .authenticationEntryPoint(jwtAuthenticationEntryPoint())
            }

            .authorizeHttpRequests {
                it.requestMatchers(HttpMethod.POST, "/api/members", "/api/login").permitAll()
                    .requestMatchers(PathRequest.toH2Console()).permitAll()
                    .anyRequest().authenticated()
            }

            .headers {
                it.frameOptions { option ->
                    option.disable()
                }
            }

            .exceptionHandling { }
            .addFilter(jwtAuthenticationFilter())
            .sessionManagement {
                it.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            }
            .build()
    }

    private fun jwtAuthenticationFilter(): JwtAuthenticationFilter {
        val jwtAuthenticationFilter = JwtAuthenticationFilter(
            jwtTokenProvider,
            objectMapper,
            authenticationManager()
        )
        jwtAuthenticationFilter.setRequiresAuthenticationRequestMatcher(
            AntPathRequestMatcher("/api/login", "POST")
        )
        jwtAuthenticationFilter.setAuthenticationFailureHandler(authenticationFailureHandler())

        return jwtAuthenticationFilter
    }

    @Bean
    fun authenticationFailureHandler(): AuthenticationFailureHandler =
        AuthenticationEntryPointFailureHandler(jwtAuthenticationEntryPoint())

    @Bean
    fun passwordEncoder(): PasswordEncoder = BCryptPasswordEncoder()

    @Bean
    fun jwtAccessDeniedHandler(): AccessDeniedHandler = JwtAccessDeniedHandler()

    @Bean
    fun jwtAuthenticationEntryPoint(): AuthenticationEntryPoint = JwtAuthenticationEntryPoint()

    @Bean
    fun authenticationManager(): AuthenticationManager = authenticationConfiguration.authenticationManager
}