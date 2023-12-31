package com.inhouse.yoursell.service

import com.inhouse.yoursell.config.toUser
import com.inhouse.yoursell.dto.AddAuctionDto
import com.inhouse.yoursell.dto.AuctionDto
import com.inhouse.yoursell.dto.toDto
import com.inhouse.yoursell.entity.auction.Auction
import com.inhouse.yoursell.entity.auction.AuctionStatus
import com.inhouse.yoursell.exceptions.NotFoundException
import com.inhouse.yoursell.repo.AuctionRepo
import com.inhouse.yoursell.repo.VehicleRepo
import jakarta.transaction.Transactional
import org.springframework.security.core.Authentication
import org.springframework.stereotype.Service
import java.time.LocalDateTime
import java.util.*


@Service
@Transactional
class AuctionService (
    private val auctionRepo: AuctionRepo,
    private val vehicleRepo: VehicleRepo
) {

    fun createAuctionWithVehicle(
        authentication: Authentication,
        payload: AddAuctionDto
    ): AuctionDto {
        val vehicleId = payload.vehicleId
        val authUser = authentication.toUser()
        val now = LocalDateTime.now()
        val endDate = now.plusDays(payload.auctionDuration)
        val vehicle = vehicleRepo.findByIdAndSeller(vehicleId, authUser).orElseThrow {
            throw NotFoundException("Vehicle not found")
        }
        val auction = Auction(
            auctionOwner = authUser,
            vehicle = vehicle,
            auctionStatus = AuctionStatus.CREATED,
            startDate = now,
            endDate = endDate,
            reservePrice = payload.reservePrice
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

    fun findById(id: UUID): AuctionDto {
        val auction = auctionRepo.findById(id).orElseThrow { throw NotFoundException("Auction not found.") }
        return auction.toDto()
    }

    fun startAuction(id: UUID): AuctionDto {
        val auction = auctionRepo.findById(id).orElseThrow { throw NotFoundException("Auction not found.") }
        if (auction.auctionStatus != AuctionStatus.CREATED) {
            throw IllegalStateException("Auction $id is not in the CREATED state, cannot START.")
        }
        auction.auctionStatus = AuctionStatus.STARTED
        return auctionRepo.save(auction).toDto()
    }

    fun closeAuction(id: UUID): AuctionDto {
        val auction = auctionRepo.findById(id).orElseThrow { throw NotFoundException("Auction not found.") }

        if (auction.auctionStatus != AuctionStatus.STARTED) {
            throw IllegalStateException("Auction $id is not in the STARTED state, cannot CLOSE.")
        }
        auction.auctionStatus = AuctionStatus.CLOSED
        return auctionRepo.save(auction).toDto()
    }

}



