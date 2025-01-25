package com.inhouse.yoursell.service

import com.inhouse.yoursell.config.toUser
import com.inhouse.yoursell.dto.*
import com.inhouse.yoursell.entity.auction.Auction
import com.inhouse.yoursell.entity.auction.AuctionDuration
import com.inhouse.yoursell.entity.auction.AuctionStatus
import com.inhouse.yoursell.exceptions.NotFoundException
import com.inhouse.yoursell.repo.AuctionRepo
import com.inhouse.yoursell.repo.ItemRepo
import jakarta.transaction.Transactional
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.security.core.Authentication
import org.springframework.stereotype.Service
import java.text.SimpleDateFormat
import java.util.*

@Service
@Transactional
class AuctionService(
    private val auctionRepo: AuctionRepo,
    private val itemRepo: ItemRepo
) {

    companion object {
        private val log: Logger = LoggerFactory.getLogger(AuctionService::class.java)
    }


    fun createAuction(authentication: Authentication, payload: CreateAuctionDto): AuctionDto {
        val authUser = authentication.toUser()

        val item = itemRepo.findByIdAndUser(payload.itemId, authUser).orElseThrow {
            throw NotFoundException("Item related to user not found.")
        }

        val startTime = System.currentTimeMillis()
        val endTime = calculateEndTime(startTime, payload.duration)

        val auction = Auction(
            user = authUser,
            item = item,
            auctionStatus = AuctionStatus.STARTED,
            expectedPrice = payload.reservePrice,
            startTime = startTime,
            endTime = endTime,
            duration = payload.duration
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

        val item = itemRepo.findById(auction.item.id).orElseThrow {
            throw Exception("Item not found.")
        }
        item.onAuction = true
        itemRepo.save(item)

        if (auction.auctionStatus != AuctionStatus.CLOSED) {
            throw IllegalStateException("Auction is not closed and cannot be restarted.")
        }

        val startTime = System.currentTimeMillis()
        val endTime = calculateEndTime(startTime, payload.duration)

        auction.startTime = startTime
        auction.endTime = endTime
        auction.auctionStatus = AuctionStatus.STARTED
        auction.duration = payload.duration
        auction.isExtended = false

        return auctionRepo.save(auction).toDto()
    }

    @Scheduled(fixedRate = 5000)  // Runs every 5 sec
    fun closeExpiredAuctionsAutomatically() {
        val currentTime = System.currentTimeMillis()
        log.info("Closing expired auctions automatically {}", currentTime)

        // Find expired auctions where the endTime is less than the current time and auctionStatus is STARTED
        val expiredAuctions = auctionRepo.findByEndTimeLessThanAndAuctionStatus(currentTime, AuctionStatus.STARTED)
        log.info("Expired auctions automatically {}", expiredAuctions)

        for (auction in expiredAuctions) {
            auction.auctionStatus = AuctionStatus.CLOSED

            // Update the associated item
            val item = itemRepo.findById(auction.item.id).orElseThrow {
                throw Exception("Item not found.")
            }
            item.onAuction = false
            itemRepo.save(item)

            // Save the updated auction
            auctionRepo.save(auction)
        }
    }


    fun extendAuctionIfNeeded(auction: Auction, bidTime: Long) {
        val timeLeft = auction.endTime - bidTime
        val extensionThreshold = 1 * 60 * 1000L // 5 minutes before end

        if (timeLeft <= extensionThreshold && !auction.isExtended) {
            auction.endTime += 10 * 60 * 1000L // Extend by 10 minutes
            auction.isExtended = true
            auctionRepo.save(auction)
        }
    }

    fun findAll(): MutableList<AuctionDto> {
        val auctionList = auctionRepo.findAll()
        return auctionList.map { it.toDto() }.toMutableList()
    }

    fun findById(id: UUID): AuctionDto {
        val auction = auctionRepo.findById(id).orElseThrow {
            throw NotFoundException("Auction not found.")
        }
        return auction.toDto()
    }

    // New method to find auctions by status
    fun findByStatus(status: AuctionStatus): List<AuctionDto> {
        val auctions = auctionRepo.findByAuctionStatus(status)
        return auctions.map { it.toDto() }
    }

    // New method to find auctions by user ID
    fun findByUser(userId: Long): List<AuctionDto> {
        val auctions = auctionRepo.findByUserId(userId)
        return auctions.map { it.toDto() }
    }

    // New method to extend auction duration
    fun extendAuctionDuration(auctionId: UUID, duration: String): AuctionDto {
        val auction = auctionRepo.findById(auctionId).orElseThrow {
            throw NotFoundException("Auction not found.")
        }

        val startTime = System.currentTimeMillis()
        val newEndTime = calculateEndTime(startTime, AuctionDuration.valueOf(duration.uppercase()))

        auction.endTime = newEndTime
        auction.duration = AuctionDuration.valueOf(duration.uppercase())
        auctionRepo.save(auction)

        return auction.toDto()
    }

    // New method to delete an auction
    fun deleteAuction(id: UUID) {
        val auction = auctionRepo.findById(id).orElseThrow {
            throw NotFoundException("Auction not found.")
        }
        auctionRepo.delete(auction)
    }

    private fun calculateEndTime(startTime: Long, duration: AuctionDuration): Long {
        return when (duration) {
            AuctionDuration.MINUTE -> startTime + 60 * 1000
            AuctionDuration.DAY -> startTime + 24 * 60 * 60 * 1000L
            AuctionDuration.WEEK -> startTime + 7 * 24 * 60 * 60 * 1000L
            AuctionDuration.TWO_WEEKS -> startTime + 14 * 24 * 60 * 60 * 1000L
            AuctionDuration.MONTH -> startTime + 30 * 24 * 60 * 60 * 1000L
        }
    }
}
