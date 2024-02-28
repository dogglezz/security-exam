package kr.dogglezz.security

data class LoginRequest(
    val username: String,
    val password: String,
)