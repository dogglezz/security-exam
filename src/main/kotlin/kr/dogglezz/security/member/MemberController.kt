package kr.dogglezz.security.member

import org.springframework.http.HttpStatus
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

@RestController
class MemberController(
    private val memberService: MemberService,
) {
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/api/members")
    fun register(@RequestBody request: RegistrationMemberRequest) {
        memberService.register(request)
    }

}

@Service
class MemberService(
    private val memberRepository: MemberRepository,
    private val passwordEncoder: PasswordEncoder,
) {

    @Transactional
    fun register(request: RegistrationMemberRequest) {
        memberRepository.findByUsername(request.name)?.run {
            throw IllegalArgumentException("duplicate Member")
        }
        val member = Member(
            username = request.name,
            password = passwordEncoder.encode(request.password),
            role = Role.MEMBER
        )
        memberRepository.save(member)
    }
}

data class RegistrationMemberRequest(
    val name: String,
    val password: String,
)