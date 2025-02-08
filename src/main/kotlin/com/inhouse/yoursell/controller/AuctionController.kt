package com.inhouse.yoursell.controller

import com.inhouse.yoursell.dto.CreateAuctionDto
import com.inhouse.yoursell.dto.RestartAuctionDto
import com.inhouse.yoursell.entity.auction.AuctionStatus
import com.inhouse.yoursell.exceptions.NotFoundException
import com.inhouse.yoursell.service.AuctionService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.*
import org.springframework.web.servlet.support.ServletUriComponentsBuilder
import java.util.*

@RestController
@RequestMapping("/api/v1/auctions")
class AuctionController(private val auctionService: AuctionService) {

    /**
     * Creates a new auction.
     *
     * @param authentication The authentication object for the current user.
     * @param payload The data required to create the auction.
     * @return A ResponseEntity containing the details of the created auction or an error message.
     */
    @PostMapping
    fun createAuction(
        authentication: Authentication,
        @RequestBody payload: CreateAuctionDto
    ): ResponseEntity<Any> {
        return try {
            val createdAuction = auctionService.createAuction(authentication, payload)
            val location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(createdAuction.id)
                .toUri()
            ResponseEntity.created(location).body(createdAuction)
        } catch (e: NotFoundException) {
            ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.message)
        } catch (e: IllegalArgumentException) {
            ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.message)
        } catch (e: Exception) {
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected error occurred: ${e.message}")
        }
    }

    /**
     * Retrieves all auctions.
     *
     * @return A ResponseEntity containing a list of all auctions or an error message.
     */
    @GetMapping
    fun getAuctions(): ResponseEntity<Any> {
        return try {
            val response = auctionService.findAll()
            ResponseEntity.ok(response)
        } catch (e: Exception) {
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred: ${e.message}")
        }
    }

    /**
     * Retrieves an auction by its ID.
     *
     * @param id The ID of the auction.
     * @return A ResponseEntity containing the auction details or an error message.
     */
    @GetMapping("/{id}")
    fun getById(@PathVariable id: UUID): ResponseEntity<Any> {
        return try {
            val response = auctionService.findById(id)
            ResponseEntity.ok(response)
        } catch (e: NotFoundException) {
            ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.message)
        } catch (e: Exception) {
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred: ${e.message}")
        }
    }

    /**
     * Restarts a closed auction.
     *
     * @param authentication The authentication object for the current user.
     * @param payload The data required to restart the auction.
     * @return A ResponseEntity containing the restarted auction details or an error message.
     */
    @PutMapping("/restart")
    fun restartAuction(
        authentication: Authentication,
        @RequestBody payload: RestartAuctionDto
    ): ResponseEntity<Any> {
        return try {
            val restartedAuction = auctionService.restartAuction(authentication, payload)
            ResponseEntity.ok(restartedAuction)
        } catch (e: NotFoundException) {
            ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.message)
        } catch (e: IllegalStateException) {
            ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.message)
        } catch (e: IllegalArgumentException) {
            ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.message)
        } catch (e: Exception) {
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected error occurred: ${e.message}")
        }
    }

    /**
     * Retrieves auctions by their status.
     *
     * @param status The status of the auctions to retrieve.
     * @return A ResponseEntity containing the list of auctions with the specified status or an error message.
     */
    @GetMapping("/status/{status}")
    fun getAuctionsByStatus(@PathVariable status: String): ResponseEntity<Any> {
        return try {
            val auctionStatus = AuctionStatus.valueOf(status.uppercase())  // Convert string to enum
            val auctions = auctionService.findByStatus(auctionStatus)
            ResponseEntity.ok(auctions)
        } catch (e: IllegalArgumentException) {
            ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid auction status.")
        } catch (e: Exception) {
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred: ${e.message}")
        }
    }

    /**
     * Retrieves auctions for a specific user.
     *
     * @param userId The ID of the user whose auctions are to be retrieved.
     * @return A ResponseEntity containing the list of auctions for the user or an error message.
     */
    @GetMapping("/user/{userId}")
    fun getAuctionsByUser(@PathVariable userId: Long): ResponseEntity<Any> {
        return try {
            val auctions = auctionService.findByUser(userId)
            ResponseEntity.ok(auctions)
        } catch (e: NotFoundException) {
            ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.message)
        } catch (e: Exception) {
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred: ${e.message}")
        }
    }

    /**
     * Extends the duration of an auction.
     *
     * @param auctionId The ID of the auction to be extended.
     * @param duration The new duration for the auction.
     * @return A ResponseEntity containing the updated auction details or an error message.
     */
    @PutMapping("/extend/{auctionId}")
    fun extendAuction(
        @PathVariable auctionId: UUID,
        @RequestParam duration: String
    ): ResponseEntity<Any> {
        return try {
            val updatedAuction = auctionService.extendAuctionDuration(auctionId, duration)
            ResponseEntity.ok(updatedAuction)
        } catch (e: NotFoundException) {
            ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.message)
        } catch (e: IllegalArgumentException) {
            ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.message)
        } catch (e: Exception) {
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected error occurred: ${e.message}")
        }
    }

    /**
     * Deletes an auction by its ID.
     *
     * @param id The ID of the auction to be deleted.
     * @return A ResponseEntity indicating the result of the delete operation.
     */
    @DeleteMapping("/{id}")
    fun deleteAuction(@PathVariable id: UUID): ResponseEntity<Any> {
        return try {
            auctionService.deleteAuction(id)
            ResponseEntity.status(HttpStatus.NO_CONTENT).build()
        } catch (e: NotFoundException) {
            ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.message)
        } catch (e: Exception) {
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred: ${e.message}")
        }
    }

    /**
     * Starts an auction.
     *
     * @param auctionId The ID of the auction to be started.
     * @return A ResponseEntity containing the details of the started auction or an error message.
     */
    @PutMapping("/start/{auctionId}")
    fun startAuction(@PathVariable auctionId: UUID): ResponseEntity<Any> {
        return try {
            val startedAuction = auctionService.startAuction(auctionId)
            ResponseEntity.ok(startedAuction)
        } catch (e: NotFoundException) {
            ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.message)
        } catch (e: IllegalStateException) {
            ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.message)
        } catch (e: Exception) {
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected error occurred: ${e.message}")
        }
    }

    /**
     * Cancels an auction before it is closed.
     *
     * @param auctionId The ID of the auction to be canceled.
     * @return A ResponseEntity containing the details of the canceled auction or an error message.
     */
    @PutMapping("/cancel/{auctionId}")
    fun cancelAuction(@PathVariable auctionId: UUID): ResponseEntity<Any> {
        return try {
            val canceledAuction = auctionService.cancelAuction(auctionId)
            ResponseEntity.ok(canceledAuction)
        } catch (e: NotFoundException) {
            ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.message)
        } catch (e: IllegalStateException) {
            ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.message)
        } catch (e: Exception) {
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected error occurred: ${e.message}")
        }
    }
}
