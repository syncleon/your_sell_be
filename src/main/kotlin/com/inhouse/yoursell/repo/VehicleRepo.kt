package com.inhouse.yoursell.repo

import com.inhouse.yoursell.entity.user.User
import com.inhouse.yoursell.entity.vehicle.Vehicle
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface VehicleRepo: JpaRepository<Vehicle, UUID> {
    fun findBySeller(user: User): List<Vehicle>
    fun findByIdAndSeller(id: UUID, user: User): Optional<Vehicle>
}