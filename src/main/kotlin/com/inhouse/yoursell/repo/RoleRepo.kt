package com.inhouse.yoursell.repo

import com.inhouse.yoursell.entity.ERole
import com.inhouse.yoursell.entity.Role
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface RoleRepo: JpaRepository<Role, Long> {
    fun findByName(name: ERole): Role
}