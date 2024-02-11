package com.inhouse.yoursell.controller

import com.inhouse.yoursell.dto.CreateBidDto
import com.inhouse.yoursell.dto.UpdateBidDto
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
    @Autowired val bidService: BidService
) {
    @PostMapping
    fun createBid(
        authentication: Authentication,
        @RequestBody payload: CreateBidDto
    ): ResponseEntity<Any> {
        return try {
            val response = bidService.createBid(payload, authentication)
            ResponseEntity.accepted().body(response)
        } catch (e: Exception) {
            ResponseEntity.badRequest().body(e.message)
        }
    }

    @GetMapping
    fun getBids(): ResponseEntity<Any> {
        return try {
            val response = bidService.findAll()
            ResponseEntity.ok().body(response)
        } catch (e: Exception) {
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred: ${e.message}")
        }
    }

    @GetMapping("/{id}")
    fun getById(
        @PathVariable id: UUID
    ): ResponseEntity<Any> {
        return try {
            val response = bidService.findById(id)
            ResponseEntity.ok().body(response)
        } catch (e: Exception) {
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred: ${e.message}")
        }
    }
}