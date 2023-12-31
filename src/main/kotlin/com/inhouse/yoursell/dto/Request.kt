package com.inhouse.yoursell.dto

import java.math.BigDecimal
import java.util.*

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
    var make: String,
    var model: String,
    var mileage: Int,
    var vin: String,
    var year: String,
    var expectedBid: Int
)

data class AddAuctionDto(
    val vehicleId: UUID,
    val reservePrice: BigDecimal,
    val auctionDuration: Long
)

data class CreateBidDto(
    val bidValue: BigDecimal,
    val auctionId: UUID
)

data class UpdateBidDto(
    val bidValue: BigDecimal
)

