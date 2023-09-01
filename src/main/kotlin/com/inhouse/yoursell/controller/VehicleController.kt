package com.inhouse.yoursell.controller

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.inhouse.yoursell.config.toUser
import com.inhouse.yoursell.dto.RegisterVehicleDto
import com.inhouse.yoursell.dto.toDto
import com.inhouse.yoursell.entity.vehicle.Vehicle
import com.inhouse.yoursell.exceptions.NotFoundException
import com.inhouse.yoursell.service.VehicleService
import org.springframework.core.io.Resource
import org.springframework.http.*
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile


@RestController
@RequestMapping("/api/v1/vehicles")
class VehicleController (
    private val vehicleService: VehicleService
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

    @GetMapping("/{id}")
    fun getVehicle(
        @PathVariable id: Long): ResponseEntity<Any>
    {
        return try {
            ResponseEntity.ok(vehicleService.findById(id))
        } catch (e: NotFoundException) {
            ResponseEntity.badRequest().body(e.message)
        }
    }

    @GetMapping("/{id}/images")
    fun getVehicleImages(@PathVariable id: Long): ResponseEntity<MutableList<String>> {
        return try {
            val images = vehicleService.findById(id).images
            ResponseEntity.ok().body(images)
        } catch (e: Exception) {
            ResponseEntity.badRequest().body(mutableListOf())
        }
    }

    @GetMapping("/display/{fileName:.+}")
    fun displayImage(
        @PathVariable("fileName") fileName: String
    ): ResponseEntity<Resource> {
        // Load the file as a Resource
        val fileResource: Resource = vehicleService.loadFile(fileName)

        // Define Content-Disposition header for inline display
        val contentDisposition = ContentDisposition
            .builder("inline")
            .filename(fileName)
            .build()

        // Return the file as a ResponseEntity
        return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_DISPOSITION, contentDisposition.toString())
            .contentType(MediaType.IMAGE_JPEG) // Adjust the MediaType as per your image type
            .body(fileResource)
    }

    @GetMapping("/all")
    fun getAll(): ResponseEntity<Any> {
        return try {
            val vehicles = vehicleService.findAll()
            ResponseEntity.ok().body(vehicles)
        } catch (e: Exception) {
            ResponseEntity.badRequest().body(e.message)
        }
    }

    @PostMapping
    fun createVehicle(
        authentication: Authentication,
        @RequestPart("payload") payload: RegisterVehicleDto,
        @RequestPart("images") images: MutableList<MultipartFile>
    ): ResponseEntity<Any> {
        return try {
            val response = vehicleService.createVehicle(authentication, payload, images)
            ResponseEntity.accepted().body("Created: $response")
        } catch (e: Exception) {
            ResponseEntity.badRequest().body(e.message)
        }
    }

    @DeleteMapping("/{id}")
    fun deleteVehicle(
        authentication: Authentication,
        @PathVariable id: Long
    ): ResponseEntity<Any> {
        return try {
            vehicleService.softDeleteVehicle(authentication,id)
            ResponseEntity.ok().body("Vehicle $id marked to be deleted")
        } catch (e: Exception) {
            ResponseEntity.badRequest().body(e.message)
        }
    }
}