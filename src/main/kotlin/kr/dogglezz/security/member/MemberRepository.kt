package kr.dogglezz.security.member

import org.springframework.data.jpa.repository.JpaRepository

interface MemberRepository : JpaRepository<Member, Long> {
    fun findByUsername(username: String): Member?
}