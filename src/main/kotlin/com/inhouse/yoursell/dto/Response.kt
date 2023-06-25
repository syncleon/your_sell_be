package com.inhouse.yoursell.dto

import com.inhouse.yoursell.entity.user.ERole
import com.inhouse.yoursell.entity.vehicle.enums.EDrivetrain
import com.inhouse.yoursell.entity.vehicle.enums.EModel
import com.inhouse.yoursell.entity.vehicle.enums.ETransmission
import jakarta.persistence.Column

data class LoginResponseDto(
    val token: String
)

data class RoleDto(
    val name: ERole
)

data class UserDto(
    val id: Long,
    val username: String,
    val email: String,
    val userRoles: MutableSet<RoleDto>,
    val vehicles: MutableList<VehicleDto>
)

data class VehicleDto(
    var id: Long,
    var producer: String,
    var model: EModel,
    var mileage: Double,
    var vin: String,
    var year: String,
    var engine: String,
    var drivetrain: EDrivetrain,
    var transmission: ETransmission,
    var bodyStyle: String,
    var exteriorColor: String,
    var interiorColor: String,
    var sellerType: String,
    var highlights: String,
    var expectedBid: Double,
    var damaged: Boolean
)