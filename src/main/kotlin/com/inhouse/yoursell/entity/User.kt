package com.inhouse.yoursell.entity

import jakarta.persistence.*

@Entity
@Table(name = "user")
data class User(
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "user_id_seq")
    @SequenceGenerator(name = "user_id_seq", allocationSize = 1)
    var id: Long = 0L,
    @Column(name = "username", nullable = false)
    var username: String = "",
    @Column(name = "password", nullable = false)
    var password: String = "",
    @Column(name = "email", nullable = false)
    var email: String = "",
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "user_roles",
        joinColumns = [JoinColumn(name = "user_id")],
        inverseJoinColumns = [JoinColumn(name = "role_id")])
    var userRoles: MutableSet<Role> = mutableSetOf()
    ) : BaseEntity() {
    override fun toString(): String {
        return "User(" +
                "id=$id, " +
                "username='$username', " +
                "email='$email," +
                "userRoles = $userRoles"
    }
}