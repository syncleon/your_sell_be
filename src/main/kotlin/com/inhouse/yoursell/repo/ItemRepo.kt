package com.inhouse.yoursell.repo

import com.inhouse.yoursell.entity.user.User
import com.inhouse.yoursell.entity.item.Item
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface ItemRepo: JpaRepository<Item, UUID> {
    fun findByIdAndUser(id: UUID, user: User): Optional<Item>
}