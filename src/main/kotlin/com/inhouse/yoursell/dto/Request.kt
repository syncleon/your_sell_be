package com.inhouse.yoursell.dto

import com.inhouse.yoursell.entity.auction.AuctionDuration
import jakarta.validation.constraints.Min
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

data class CreateItemDto(
    var make: String,
    var model: String,
    var mileage: String? = null,  // Optional field
    var year: String,
    var price: Double,
    var exteriorColor: String? = null,  // Optional field
    var interiorColor: String? = null,  // Optional field
    var engineSize: String? = null,  // Optional field
    var fuelType: String? = null,  // Optional field
    var transmission: String? = null,  // Optional field
    var condition: String? = null,  // Optional field
    var drivetrain: String? = null,  // Optional field
    var bodyStyle: String? = null,  // Optional field
    var location: String? = null,  // Optional field
    var description: String? = null,  // Optional field
    var vin: String? = null,  // Optional field
    var onAuction: Boolean = false,
    var isSold: Boolean = false
)

data class CreateAuctionDto(
    val itemId: UUID,
    @field:Min(0)
    val reservePrice: Double,
    val duration: AuctionDuration,
    val isExtended: Boolean = false,
    val isAutoExtendEnabled: Boolean = false,
    val autoExtendDuration: Long = 1 * 60 * 1000L
)

data class RestartAuctionDto(
    val auctionId: UUID,
    @field:Min(0)
    val reservePrice: Double,
    val duration: AuctionDuration,
    val isExtended: Boolean = false,
    val isAutoExtendEnabled: Boolean = false,
    val autoExtendDuration: Long = 1 * 60 * 1000L
)

data class CreateBidDto(
    val bidValue: Double,
    val auctionId: UUID
)
