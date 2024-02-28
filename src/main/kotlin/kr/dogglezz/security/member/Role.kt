package kr.dogglezz.security.member

enum class Role(
    val role: String,
    val description: String
) {

    ADMIN("ROLE_ADMIN", "관리자"), MEMBER("ROLE_MEMBER", "유저");

    companion object {
        fun isSupport(authority: String?): Boolean = entries.any { it.role == authority }
    }

}