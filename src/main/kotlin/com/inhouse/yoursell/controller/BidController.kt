package com.inhouse.yoursell.controller

import com.inhouse.yoursell.dto.CreateBidDto
import com.inhouse.yoursell.service.BidService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.*
import java.util.*

@RestController
@RequestMapping("/api/v1/bids")
class BidController(
    @Autowired private val bidService: BidService
) {

    @PostMapping
    fun createBid(
        authentication: Authentication,
        @RequestBody payload: CreateBidDto
    ): ResponseEntity<Any> {
        return handleRequest {
            bidService.placeBid(payload, authentication)
        }
    }

    @GetMapping
    fun getBids(): ResponseEntity<Any> {
        return handleRequest {
            bidService.getAllBids()
        }
    }

    @GetMapping("/{id}")
    fun getById(
        @PathVariable id: UUID
    ): ResponseEntity<Any> {
        return handleRequest {
            bidService.getBidById(id)
        }
    }

    private fun handleRequest(action: () -> Any): ResponseEntity<Any> {
        return try {
            val response = action()
            ResponseEntity.ok().body(response)
        } catch (e: Exception) {
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred: ${e.message}")
        }
    }
}
