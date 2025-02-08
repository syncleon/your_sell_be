package com.inhouse.yoursell.dto

import com.inhouse.yoursell.entity.auction.AuctionDuration
import com.inhouse.yoursell.entity.auction.AuctionStatus
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
    val items: MutableList<ItemDto>,
    val bids: MutableList<BidDto>
)

data class ItemDto(
    var id: UUID,
    var make: String,
    var model: String,
    var mileage: String?,
    var year: String,
    var price: Double,
    var engine: String?,
    var fuelType: String?,
    var transmission: String?,
    var condition: String?,
    var bodyStyle: String?,
    var drivetrain: String?,
    var location: String?,
    var description: String?,
    var vin: String?,
    var exteriorColor: String?,
    var interiorColor: String?,
    var imagesFeatured: List<String>,
    var imagesExterior: List<String>,
    var imagesInterior: List<String>,
    var imagesMechanical: List<String>,
    var imagesOther: List<String>,
    var isSold: Boolean,
    var onAuction: Boolean,
    var userId: Long?,
    var username: String,
    var auction: AuctionDto?
)

data class BidDto (
    val id: UUID,
    val userId: Long,
    val value: Double,
    val isWinning: Boolean,
    val auctionId: UUID
)

data class AuctionDto(
    val id: UUID,
    val userId: Long,
    val itemId: UUID,
    val auctionStatus: AuctionStatus,
    val bids: MutableList<BidDto>,
    val currentHighestBid: Double,
    val expectedPrice: Double,
    val reservePrice: Double?,
    val winningBidId: UUID?,
    val duration: AuctionDuration,
    val startTime: Long,
    val endTime: Long,
    val bidCount: Int,
    val isExtended: Boolean,
    val isAutoExtendEnabled: Boolean,
    val autoExtendDuration: Long
)

