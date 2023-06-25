package com.inhouse.yoursell.repo

import com.inhouse.yoursell.entity.user.ERole
import com.inhouse.yoursell.entity.user.Role
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface RoleRepo: JpaRepository<Role, Long> {
    fun findByName(name: ERole): Role
}