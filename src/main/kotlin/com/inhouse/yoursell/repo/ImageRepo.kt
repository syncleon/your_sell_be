package com.inhouse.yoursell.repo

import com.inhouse.yoursell.entity.image.Image
import com.inhouse.yoursell.entity.vehicle.Vehicle
import org.springframework.data.jpa.repository.JpaRepository
import java.util.Optional

interface ImageRepo : JpaRepository<Image, Long> {
    fun findByName(name: String): Optional<Image>
    fun findByVehicleId(vehicleId: Long): Optional<List<Image>>
}