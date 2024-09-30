package com.inhouse.yoursell.controller

import com.inhouse.yoursell.dto.CreateItemDto
import com.inhouse.yoursell.exceptions.BadRequestException
import com.inhouse.yoursell.exceptions.NotFoundException
import com.inhouse.yoursell.service.ItemService
import org.springframework.core.io.Resource
import org.springframework.http.*
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
    fun getItem(@PathVariable id: UUID): ResponseEntity<Any> {
        return try {
            ResponseEntity.ok(itemService.findById(id))
        } catch (e: NotFoundException) {
            ResponseEntity.badRequest().body(e.message)
        }
    }

    @GetMapping("/{id}/images")
    fun getImages(@PathVariable id: UUID): ResponseEntity<Map<String, MutableList<String>>> {
        return try {
            val images = itemService.getImagesById(id)
            ResponseEntity.ok(images)
        } catch (e: Exception) {
            ResponseEntity.badRequest().body(emptyMap())
        }
    }

    @GetMapping("/{id}/images/{category}")
    fun getImagesByCategory(
        @PathVariable id: UUID,
        @PathVariable category: String
    ): ResponseEntity<MutableList<String>> {
        return try {
            val images = itemService.getImagesByCategory(id, category)
            if (images.isNotEmpty()) {
                ResponseEntity.ok(images)
            } else {
                ResponseEntity.notFound().build()
            }
        } catch (e: Exception) {
            ResponseEntity.badRequest().body(mutableListOf())
        }
    }

    @GetMapping("/{id}/images/{category}/{fileName}")
    fun getImageByCategoryAndFileName(
        @PathVariable id: UUID,
        @PathVariable category: String,
        @PathVariable fileName: String
    ): ResponseEntity<Resource> {
        return try {
            val fileResource = itemService.getImageByCategoryAndFileName(id, category, fileName)
            val contentDisposition = ContentDisposition.builder("inline")
                .filename(fileName)
                .build()

            ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, contentDisposition.toString())
                .contentType(MediaType.IMAGE_JPEG) // adjust for different image types if needed
                .body(fileResource)
        } catch (e: NotFoundException) {
            // Return 404 if file not found
            ResponseEntity.notFound().build()
        } catch (e: IllegalArgumentException) {
            // Return 400 for invalid inputs
            ResponseEntity.badRequest().body(null)
        } catch (e: Exception) {
            // Log unexpected exceptions and return 500
            e.printStackTrace()
            ResponseEntity.status(500).body(null)
        }
    }

    @PostMapping
    fun createItem(
        authentication: Authentication,
        @RequestPart("payload") payload: CreateItemDto,
        @RequestPart("images_featured") imagesFeatured: List<MultipartFile>,
        @RequestPart("images_exterior") imagesExterior: List<MultipartFile>? = emptyList(),
        @RequestPart("images_interior") imagesInterior: List<MultipartFile>? = emptyList(),
        @RequestPart("images_mechanical") imagesMechanical: List<MultipartFile>? = emptyList(),
        @RequestPart("images_other") imagesOther: List<MultipartFile>? = emptyList()
    ): ResponseEntity<Any> {
        return try {
            if (imagesFeatured.isEmpty()) {
                throw BadRequestException("At least one featured image should be provided.")
            }
            val response = itemService.createItem(
                authentication,
                payload,
                imagesFeatured,
                imagesExterior ?: emptyList(),
                imagesInterior ?: emptyList(),
                imagesMechanical ?: emptyList(),
                imagesOther ?: emptyList()
            )
            ResponseEntity.status(HttpStatus.CREATED).body("Created: $response")
        } catch (e: BadRequestException) {
            ResponseEntity.badRequest().body(e.message)
        }
    }
}