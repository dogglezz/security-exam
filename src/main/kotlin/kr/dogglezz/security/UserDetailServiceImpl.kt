package kr.dogglezz.security

import kr.dogglezz.security.member.MemberRepository
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service

@Service
class UserDetailServiceImpl(
    private val memberRepository: MemberRepository
): UserDetailsService {
    override fun loadUserByUsername(username: String): UserDetails {
        val member = (memberRepository.findByUsername(username)
            ?: throw UsernameNotFoundException("not found"))
        return User.builder()
            .username(member.id.toString())
            .password(member.password)
            .authorities(member.role.role)
            .build()
    }
}