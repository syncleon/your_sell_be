package com.inhouse.yoursell.controller

import com.inhouse.yoursell.dto.CreateItemDto
import com.inhouse.yoursell.exceptions.NotFoundException
import com.inhouse.yoursell.service.ItemService
import org.springframework.core.io.Resource
import org.springframework.http.ContentDisposition
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import java.util.*


@RestController
@RequestMapping("/api/v1/items")
class ItemController (
    private val itemService: ItemService
) {
    @GetMapping
    fun getItems(): ResponseEntity<Any> {
        return try {
            val items = itemService.findAll()
            ResponseEntity.ok().body(items)
        } catch (e: Exception) {
            ResponseEntity.badRequest().body(e.message)
        }
    }

    @GetMapping("/{id}")
    fun getItem(@PathVariable id: UUID): ResponseEntity<Any>
    {
        return try {
            ResponseEntity.ok(itemService.findById(id))
        } catch (e: NotFoundException) {
            ResponseEntity.badRequest().body(e.message)
        }
    }

    @GetMapping("/{id}/images")
    fun getImages(@PathVariable id: UUID): ResponseEntity<MutableList<String>> {
        return try {
            val images = itemService.findById(id).images
            ResponseEntity.ok().body(images)
        } catch (e: Exception) {
            ResponseEntity.badRequest().body(mutableListOf())
        }
    }

    @GetMapping("/display/{folder}/{fileName:.+}")
    fun displayImage(
        @PathVariable("folder") vehicleId: String,
        @PathVariable("fileName") fileName: String
    ): ResponseEntity<Resource> {
        val fileResource: Resource = itemService.loadFile(fileName, vehicleId)
        val contentDisposition = ContentDisposition
            .builder("inline")
            .filename(fileName)
            .build()
        return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_DISPOSITION, contentDisposition.toString())
            .contentType(MediaType.IMAGE_JPEG)
            .body(fileResource)
    }

    @PostMapping
    fun createItem(
        authentication: Authentication,
        @RequestPart("payload") payload: CreateItemDto,
        @RequestPart("images") images: MutableList<MultipartFile>
    ): ResponseEntity<Any> {
        return try {
            val response = itemService.createItem(authentication, payload, images)
            ResponseEntity.accepted().body("Created: $response")
        } catch (e: Exception) {
            ResponseEntity.badRequest().body(e.message)
        }
    }
}