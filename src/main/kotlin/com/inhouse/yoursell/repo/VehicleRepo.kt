package com.inhouse.yoursell.repo

import com.inhouse.yoursell.entity.user.User
import com.inhouse.yoursell.entity.vehicle.Vehicle
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface VehicleRepo: JpaRepository<Vehicle, Long> {
    fun findBySeller(user: User): List<Vehicle>
    fun existsByVinAndSeller(vin: String, user: User): Boolean
}