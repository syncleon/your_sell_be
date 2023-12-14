package com.inhouse.yoursell.entity.user

import jakarta.persistence.*

@Entity
@Table(name = "roles")
data class Role (
    @Id
    @GeneratedValue(
        strategy = GenerationType.IDENTITY
    )
    var id: Long,

    @Enumerated(
        EnumType.STRING
    )
    @Column(
        name = "name"
    )
    var name: ERole = ERole.ROLE_USER
)