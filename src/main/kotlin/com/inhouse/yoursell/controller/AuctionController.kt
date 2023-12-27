package com.inhouse.yoursell.controller

import com.inhouse.yoursell.dto.AddAuctionDto
import com.inhouse.yoursell.service.AuctionService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController


@RestController
@RequestMapping("/api/v1/auctions")
class AuctionController (
    private val auctionService: AuctionService
)

{
    @PostMapping
    fun createAuction(
        authentication: Authentication,
        @RequestBody payload: AddAuctionDto
    ): ResponseEntity<Any> {
        return try{
            val response = auctionService.addAuction(authentication, payload)
            ResponseEntity.accepted().body("Created $response")
        }
        catch (e: Exception) {
            ResponseEntity.badRequest().body(e.message)
        }

    }

    @GetMapping
    fun getAuctions(): ResponseEntity<Any> {
        return try {
            val response = auctionService.getAuctions()
            ResponseEntity.ok().body(response)
        } catch (e: Exception) {
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred: ${e.message}")
        }
    }


}