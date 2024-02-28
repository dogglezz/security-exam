package kr.dogglezz.security.member

import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.GeneratedValue
import jakarta.persistence.Id
import jakarta.persistence.Table

@Entity
@Table(name = "members")
class Member(
    @Id
    @GeneratedValue
    val id: Long = 0L,

    val username: String,
    val password: String,

    @Enumerated(EnumType.STRING)
    val role: Role
)