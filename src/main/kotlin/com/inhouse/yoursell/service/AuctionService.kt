package com.inhouse.yoursell.service

import com.inhouse.yoursell.config.toUser
import com.inhouse.yoursell.dto.AddAuctionDto
import com.inhouse.yoursell.dto.AuctionDto
import com.inhouse.yoursell.dto.UserDto
import com.inhouse.yoursell.dto.toDto
import com.inhouse.yoursell.entity.auction.Auction
import com.inhouse.yoursell.exceptions.NotFoundException
import com.inhouse.yoursell.repo.AuctionRepo
import com.inhouse.yoursell.repo.VehicleRepo
import jakarta.transaction.Transactional
import org.springframework.security.core.Authentication
import org.springframework.stereotype.Service


@Service
@Transactional
class AuctionService (
    private val auctionRepo: AuctionRepo,
    private val vehicleRepo: VehicleRepo
) {

    fun addAuction(
        authentication: Authentication,
        payload: AddAuctionDto
    ): AuctionDto {
        val vehicleId = payload.vehicleId
        val authUser = authentication.toUser()
        val vehicle = vehicleRepo.findByIdAndSeller(vehicleId, authUser).orElseThrow {
            throw NotFoundException("Vehicle not found")
        }
        val auction = Auction(
            auctionOwner = authUser,
            vehicle = vehicle
        )
        vehicle.onSale = true
        vehicleRepo.save(vehicle)
        val savedAuction = auctionRepo.save(auction)
        return savedAuction.toDto()
    }

    fun getAuctions(): MutableList<AuctionDto> {
        val auctionList = auctionRepo.findAll()
        val auctionsDtoList: MutableList<AuctionDto> = mutableListOf()
        auctionList.forEach { auction ->
            val auctionDto = auction.toDto()
            auctionsDtoList.add(auctionDto)
        }
        return auctionsDtoList
    }
}



