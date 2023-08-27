package com.inhouse.yoursell.service

import com.inhouse.yoursell.config.toUser
import com.inhouse.yoursell.dto.RegisterVehicleDto
import com.inhouse.yoursell.dto.VehicleDto
import com.inhouse.yoursell.dto.toDto
import com.inhouse.yoursell.entity.user.User
import com.inhouse.yoursell.entity.vehicle.Vehicle
import com.inhouse.yoursell.exceptions.AlreadyExistsException
import com.inhouse.yoursell.exceptions.NotFoundException
import com.inhouse.yoursell.repo.UserRepo
import com.inhouse.yoursell.repo.VehicleRepo
import jakarta.transaction.Transactional
import org.springframework.security.core.Authentication
import org.springframework.stereotype.Service

@Service
@Transactional
class VehicleService (
    private val vehicleRepo: VehicleRepo,
    private val userRepo: UserRepo
) {

    fun findById(id: Long): VehicleDto {
        val vehicle = vehicleRepo.findById(id).orElseThrow {
            throw NotFoundException("Vehicle $id not found!")
        }
        return vehicle.toDto()
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
            expectedBid = payload.expectedBid,
        )
        return vehicleRepo.save(vehicle).toDto()
    }

    fun softDeleteVehicle(authentication: Authentication, id: Long) {
        val authUser = authentication.toUser()
        val vehicle = vehicleRepo.findByIdAndSeller(id, authUser).orElseThrow {
            throw NotFoundException("Vehicle $id not found!")
        }
        vehicle.deleted = true
        vehicleRepo.save(vehicle)
    }
}