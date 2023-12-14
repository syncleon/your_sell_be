package com.inhouse.yoursell.dto

import com.inhouse.yoursell.entity.auction.Bid
import com.inhouse.yoursell.entity.user.ERole
import java.util.UUID

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
    var id: UUID,
    var make: String,
    var model: String,
    var mileage: Int,
    var vin: String,
    var year: String,
    var expectedBid: Int,
    var damaged: Boolean,
    var sellerId: Long,
    var sellerUsername: String,
    var images: MutableList<String>,
    var onSale: Boolean,
    var sold: Boolean,
    var deleted: Boolean,
)

data class BidDto(
    var id: UUID,
    var bidderId: Long,
    var bidAmount: Double,
    var auctionId: UUID
)

data class AuctionDto(
    var id: UUID,
    var auctionCreator: UserDto,
    var vehicle: VehicleDto,
    var bids: MutableList<BidDto>
)