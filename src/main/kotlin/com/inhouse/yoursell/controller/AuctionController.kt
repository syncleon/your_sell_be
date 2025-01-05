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

    // Create a new auction
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

    // Get all auctions
    @GetMapping
    fun getAuctions(): ResponseEntity<Any> {
        return try {
            val response = auctionService.findAll()
            ResponseEntity.ok(response)
        } catch (e: Exception) {
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred: ${e.message}")
        }
    }

    // Get auction by ID
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

    // Restart a closed auction
    @PutMapping("/restart")
    fun restartAuction(
        authentication: Authentication,
        @RequestBody payload: RestartAuctionDto
    ): ResponseEntity<Any> {
        return try {
            val restartedAuction = auctionService.restartClosedAuctionById(authentication, payload)
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

    // Get auctions by status (e.g., active, closed)
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

    // Get auctions by user ID
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

    // Extend auction duration
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

    // Delete auction by ID
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
}
