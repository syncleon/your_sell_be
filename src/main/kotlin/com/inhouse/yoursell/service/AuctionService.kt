package com.inhouse.yoursell.service

import com.inhouse.yoursell.config.toUser
import com.inhouse.yoursell.dto.*
import com.inhouse.yoursell.entity.auction.Auction
import com.inhouse.yoursell.entity.auction.AuctionStatus
import com.inhouse.yoursell.exceptions.NotFoundException
import com.inhouse.yoursell.repo.AuctionRepo
import com.inhouse.yoursell.repo.ItemRepo
import jakarta.transaction.Transactional
import org.springframework.security.core.Authentication
import org.springframework.stereotype.Service
import java.util.*


@Service
@Transactional
class AuctionService (
    private val auctionRepo: AuctionRepo,
    private val itemRepo: ItemRepo
) {
    fun createAuction(
        authentication: Authentication,
        payload: CreateAuctionDto
    ): AuctionDto {
        val itemId = payload.itemId
        val auctionDuration = payload.duration
        val authUser = authentication.toUser()

        val item = itemRepo.findByIdAndUser(itemId, authUser).orElseThrow {
            throw NotFoundException("Item related to user not found.")
        }
        val timestampMinute = 60 * 1000L
        val timestampHour = 60 * 60 * 1000L
        val timestampDay = 24 * 60 * 60 * 1000L
        val timestampWeek = 7 * 24 * 60 * 60 * 1000L
        val timestampMonth = 30 * 24 * 60 * 60 * 1000L

        val startTime = System.currentTimeMillis()
        val endTime: Long = when (auctionDuration) {
            "minute" -> {
                startTime + timestampMinute
            }
            "hour" -> {
                startTime + timestampHour
            }
            "day" -> {
                startTime + timestampDay
            }
            "week" -> {
                startTime + timestampWeek
            }

            "month" -> {
                startTime + timestampMonth
            }

            else -> throw IllegalArgumentException("Unsupported duration: ${payload.duration}")
        }
        val auction = Auction(
            user = authUser,
            item = item,
            auctionStatus = AuctionStatus.STARTED,
            expectedPrice = payload.reservePrice,
            startTime = startTime,
            endTime = endTime
        )
        item.onAuction = true

        itemRepo.save(item)

        return auctionRepo.save(auction).toDto()
    }

    fun restartClosedAuctionById(authentication: Authentication, payload: RestartAuctionDto): AuctionDto {
        val authUser = authentication.toUser()
        val auction = auctionRepo.findByIdAndUser(payload.auctionId, authUser).orElseThrow {
            throw NotFoundException("Auction for user not found.")
        }

        if (auction.auctionStatus != AuctionStatus.CLOSED) {
            throw IllegalStateException("Auction is not closed and cannot be restarted.")
        }

        val duration = payload.duration
        val startTime = System.currentTimeMillis()
        val endTime: Long = when (duration) {
            "minute" -> startTime +  60 * 1000L
            "hour" -> startTime + 60 * 60 * 1000L
            "day" -> startTime + 24 * 60 * 60 * 1000L
            "week" -> startTime + 7 * 24 * 60 * 60 * 1000L
            "month" -> startTime + 30 * 24 * 60 * 60 * 1000L
            else -> throw IllegalArgumentException("Unsupported duration: ${payload.duration}")
        }
        auction.startTime = startTime
        auction.endTime = endTime
        auction.auctionStatus = AuctionStatus.STARTED
        return auctionRepo.save(auction).toDto()
    }

    fun findAll(): MutableList<AuctionDto> {
        val auctionList = auctionRepo.findAll()
        val auctionsDtoList: MutableList<AuctionDto> = mutableListOf()
        auctionList.forEach { auction ->
            val auctionDto = auction.toDto()
            auctionsDtoList.add(auctionDto)
        }
        return auctionsDtoList
    }

    fun findById(id: UUID): AuctionDto {
        val auction = auctionRepo.findById(id).orElseThrow {
            throw NotFoundException("Auction not found.")
        }
        return auction.toDto()
    }

    fun closeAuctions() {
        val currentTime = System.currentTimeMillis()
        val expiredAuctions = auctionRepo.findByEndTimeAndAuctionStatus(currentTime, AuctionStatus.STARTED)
        for (auction in expiredAuctions) {
            auction.auctionStatus = AuctionStatus.CLOSED
            val item = itemRepo.findById(auction.item.id).orElseThrow {
                throw Exception("Vehicle not found.")
            }
            item.onAuction = false
            itemRepo.save(item)
            auctionRepo.save(auction).toDto()
        }
    }

}



