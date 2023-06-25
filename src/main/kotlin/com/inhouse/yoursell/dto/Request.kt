package com.inhouse.yoursell.dto

import com.inhouse.yoursell.entity.vehicle.enums.EModel

data class LoginUserDto(
    val username: String,
    val password: String,
)

data class RegisterUserDto(
    val username: String,
    val password: String,
    val email: String
)

data class RegisterVehicleDto(
    val vin: String,
    val year: String,
    val producer: String,
    val mileage: Double,
    val highlights: String,
    val expectedBid: Double,
    val damaged: Boolean
)
