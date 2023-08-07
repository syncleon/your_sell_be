package com.inhouse.yoursell.service

import com.inhouse.yoursell.config.toUser
import com.inhouse.yoursell.dto.RegisterVehicleDto
import com.inhouse.yoursell.dto.VehicleDto
import com.inhouse.yoursell.dto.toDto
import com.inhouse.yoursell.entity.user.User
import com.inhouse.yoursell.entity.vehicle.Vehicle
import com.inhouse.yoursell.exceptions.AlreadyExistsException
import com.inhouse.yoursell.exceptions.NotFoundException
import com.inhouse.yoursell.repo.VehicleRepo
import org.springframework.security.core.Authentication
import org.springframework.stereotype.Service

@Service
class VehicleService (
    private val vehicleRepo: VehicleRepo
) {

    fun findById(id: Long) {
        val vehicle = vehicleRepo.findById(id)
        if (vehicle.isEmpty) {
            throw NotFoundException("Vehicle $id not found!")
        }
    }

    fun findAll(): MutableList<VehicleDto> {
        val vehicleList = vehicleRepo.findAll()
        val vehicleDtoList = mutableListOf<VehicleDto>()
        vehicleList.forEach { vehicle ->
            val vehicleDto = vehicle.toDto()
            vehicleDtoList.add(vehicleDto)
        }
        return vehicleDtoList
    }

    fun findBySeller(user: User): List<Vehicle> {
        return vehicleRepo.findBySeller(user)
    }

    fun createVehicle(
        authentication: Authentication,
        payload: RegisterVehicleDto
    ): VehicleDto {
        val authUser = authentication.toUser()
        if (vehicleRepo.existsByVinAndSeller(payload.vin, authUser)) {
            throw AlreadyExistsException("Car with this VIN already added!")
        }
        val vehicle = Vehicle(
            seller = authUser,
            make = payload.make,
            model = payload.model,
            mileage = payload.mileage,
            vin = payload.vin,
            year = payload.year,
            images = payload.images
        )
        return vehicleRepo.save(vehicle).toDto()
    }
}