package com.inhouse.yoursell.controller

import com.inhouse.yoursell.config.toUser
import com.inhouse.yoursell.dto.RegisterVehicleDto
import com.inhouse.yoursell.dto.toDto
import com.inhouse.yoursell.entity.vehicle.Vehicle
import com.inhouse.yoursell.service.VehicleService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController


@RestController
@RequestMapping("/api/v1/vehicles")
class VehicleController (
    @Autowired
    val vehicleService: VehicleService
) {
    @GetMapping
    fun getVehicles(authentication: Authentication): ResponseEntity<Any> {
        val authUser = authentication.toUser()
        return try {
            ResponseEntity.ok(vehicleService.findBySeller(authUser).map { vehicle: Vehicle -> vehicle.toDto() })
        } catch (e: Exception) {
            ResponseEntity.badRequest().body(e.message)
        }
    }

    @PostMapping
    fun createVehicle(
        authentication: Authentication,
        @RequestBody payload: RegisterVehicleDto
    ): ResponseEntity<Any> {
        return try {
            vehicleService.createVehicle(authentication, payload)
            ResponseEntity.accepted().body("Created!" +
                    "\n" +
                    "$payload")
        } catch (e: Exception) {
            ResponseEntity.badRequest().body(e.message)
        }
    }
}