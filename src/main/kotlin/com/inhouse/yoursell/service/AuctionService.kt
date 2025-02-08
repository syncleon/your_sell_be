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
import toDto
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

    /**
     * Creates a new auction.
     *
     * This method creates a new auction for an item by calculating the start time and the end time
     * based on the provided duration. The item is marked as being on auction.
     *
     * @param auth The authentication object for the current user.
     * @param payload The data required to create the auction.
     * @return The details of the created auction as an AuctionDto.
     */
    fun createAuction(auth: Authentication, payload: CreateAuctionDto): AuctionDto {
        val authUser = auth.toUser()
        val item = itemRepo.findByIdAndUser(
            payload.itemId,
            authUser
        ).orElseThrow {
            throw NotFoundException("Item related to user not found.")
        }
        val startTime = System.currentTimeMillis()
        val endTime = calculateEndTime(startTime, payload.duration)
        val auction = Auction(
            user = authUser,
            item = item,
            auctionStatus = AuctionStatus.CREATED,
            expectedPrice = payload.reservePrice,
            startTime = startTime,
            endTime = endTime,
            duration = payload.duration
        )
        item.onAuction = true
        itemRepo.save(item)
        return auctionRepo.save(auction).toDto()
    }

    /**
     * Starts an auction that has been created but not started.
     *
     * This method will set the auction status to STARTED and initiate the auction.
     *
     * @param auctionId The ID of the auction to be started.
     * @return The details of the started auction as an AuctionDto.
     */
    fun startAuction(auctionId: UUID): AuctionDto {
        val auction = auctionRepo.findById(auctionId).orElseThrow {
            throw NotFoundException("Auction not found.")
        }

        if (auction.auctionStatus != AuctionStatus.CREATED) {
            throw IllegalStateException("Auction must be in CREATED status to be started.")
        }

        auction.auctionStatus = AuctionStatus.STARTED
        auction.startTime = System.currentTimeMillis()
        auction.endTime = calculateEndTime(auction.startTime, auction.duration)

        auctionRepo.save(auction)
        return auction.toDto()
    }

    /**
     * Cancels an auction before it is closed.
     *
     * This method sets the auction status to CANCELLED and marks the auction as no longer active.
     *
     * @param auctionId The ID of the auction to be canceled.
     * @return The details of the canceled auction as an AuctionDto.
     */
    fun cancelAuction(auctionId: UUID): AuctionDto {
        val auction = auctionRepo.findById(auctionId).orElseThrow {
            throw NotFoundException("Auction not found.")
        }

        if (auction.auctionStatus == AuctionStatus.CLOSED || auction.auctionStatus == AuctionStatus.SOLD) {
            throw IllegalStateException("Auction cannot be canceled after it has ended.")
        }

        auction.auctionStatus = AuctionStatus.CANCELLED

        // Optionally, update the item to indicate it is no longer on auction
        val item = itemRepo.findById(auction.item.id).orElseThrow {
            throw Exception("Item not found.")
        }
        item.onAuction = false
        itemRepo.save(item)

        auctionRepo.save(auction)
        return auction.toDto()
    }

    /**
     * Restarts a closed auction.
     *
     * This method allows restarting a closed auction by updating the start and end times,
     * and resetting the auction status to STARTED. It also checks if the auction is closed before
     * allowing a restart.
     *
     * @param authentication The authentication object for the current user.
     * @param payload The data required to restart the auction.
     * @return The details of the restarted auction as an AuctionDto.
     */
    fun restartAuction(authentication: Authentication, payload: RestartAuctionDto): AuctionDto {
        val authUser = authentication.toUser()
        val auction = auctionRepo.findByIdAndUser(payload.auctionId, authUser).orElseThrow {
            throw NotFoundException("Auction for user not found.")
        }
        val item = itemRepo.findById(auction.item.id).orElseThrow {
            throw Exception("Item not found.")
        }
        item.onAuction = true
        itemRepo.save(item)

        if (auction.auctionStatus != AuctionStatus.CANCELLED && auction.auctionStatus != AuctionStatus.CLOSED) {
            throw IllegalStateException("Auction must be either CANCELLED or CLOSED to be restarted.")
        }

        val startTime = System.currentTimeMillis()
        val endTime = calculateEndTime(startTime, payload.duration)

        auction.startTime = startTime
        auction.endTime = endTime
        auction.auctionStatus = AuctionStatus.STARTED
        auction.duration = payload.duration
        auction.reservePrice = payload.reservePrice
        auction.isExtended = false

        return auctionRepo.save(auction).toDto()
    }

    /**
     * Closes expired auctions automatically.
     *
     * This method runs periodically to check and close auctions that have expired (where the
     * end time is in the past and the auction status is still "STARTED"). It also marks the
     * associated item as no longer being on auction.
     */
    @Scheduled(fixedRate = 5000)  // Runs every 5 sec
    fun closeExpired() {
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

    /**
     * Extends an auction if the bid is placed within the last minute of the auction.
     *
     * This method checks if the time left in the auction is less than or equal to a specified
     * threshold and extends the auction if needed.
     *
     * @param auction The auction to be extended.
     * @param bidTime The timestamp of the bid being placed.
     */
    fun extendAuctionIfNeeded(auction: Auction, bidTime: Long) {
        val timeLeft = auction.endTime - bidTime
        val extensionThreshold = 1 * 60 * 1000L // 1 minute before end

        if (timeLeft <= extensionThreshold && !auction.isExtended) {
            auction.endTime += 10 * 60 * 1000L // Extend by 10 minutes
            auction.isExtended = true
            auctionRepo.save(auction)
        }
    }

    /**
     * Retrieves all auctions.
     *
     * @return A list of all auctions as AuctionDto.
     */
    fun findAll(): MutableList<AuctionDto> {
        val auctionList = auctionRepo.findAll()
        return auctionList.map { it.toDto() }.toMutableList()
    }

    /**
     * Retrieves an auction by its ID.
     *
     * @param id The ID of the auction.
     * @return The auction as an AuctionDto.
     */
    fun findById(id: UUID): AuctionDto {
        val auction = auctionRepo.findById(id).orElseThrow {
            throw NotFoundException("Auction not found.")
        }
        return auction.toDto()
    }

    /**
     * Retrieves auctions by their status.
     *
     * @param status The status of the auctions to retrieve.
     * @return A list of auctions with the specified status.
     */
    fun findByStatus(status: AuctionStatus): List<AuctionDto> {
        val auctions = auctionRepo.findByAuctionStatus(status)
        return auctions.map { it.toDto() }
    }

    /**
     * Retrieves auctions for a specific user.
     *
     * @param userId The ID of the user whose auctions are to be retrieved.
     * @return A list of auctions belonging to the specified user.
     */
    fun findByUser(userId: Long): List<AuctionDto> {
        val auctions = auctionRepo.findByUserId(userId)
        return auctions.map { it.toDto() }
    }

    /**
     * Extends the duration of an auction.
     *
     * This method extends the auction's end time based on the new duration provided.
     *
     * @param auctionId The ID of the auction to be extended.
     * @param duration The new duration for the auction.
     * @return The updated auction as an AuctionDto.
     */
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

    /**
     * Deletes an auction by its ID.
     *
     * @param id The ID of the auction to be deleted.
     */
    fun deleteAuction(id: UUID) {
        val auction = auctionRepo.findById(id).orElseThrow {
            throw NotFoundException("Auction not found.")
        }
        auctionRepo.delete(auction)
    }

    /**
     * Calculates the end time for an auction based on the start time and duration.
     *
     * @param startTime The start time of the auction in milliseconds.
     * @param duration The duration of the auction (e.g., 1 minute, 1 hour).
     * @return The calculated end time in milliseconds.
     */
    private fun calculateEndTime(startTime: Long, duration: AuctionDuration): Long {
        return when (duration) {
            AuctionDuration.MINUTE -> startTime + 60 * 1000 // 1 minute
            AuctionDuration.HOUR -> startTime + 60 * 60 * 1000 // 1 hour
            AuctionDuration.DAY -> startTime + 24 * 60 * 60 * 1000L // 1 day
            AuctionDuration.WEEK -> startTime + 7 * 24 * 60 * 60 * 1000L // 1 week
            AuctionDuration.TWO_WEEKS -> startTime + 14 * 24 * 60 * 60 * 1000L // 2 weeks
            AuctionDuration.MONTH -> startTime + 30 * 24 * 60 * 60 * 1000L // 1 month
        }
    }
}
