package com.inhouse.yoursell.controller

import com.inhouse.yoursell.config.toUser
import com.inhouse.yoursell.dto.ImageDto
import com.inhouse.yoursell.dto.toDto
import com.inhouse.yoursell.entity.image.Image
import com.inhouse.yoursell.exceptions.NotFoundException
import com.inhouse.yoursell.repo.ImageRepo
import com.inhouse.yoursell.service.ImageService
import org.springframework.core.io.Resource
import org.springframework.http.*
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile

@RestController
@RequestMapping("/api/v1/images")
class ImageController(
    private val imageService: ImageService,
    private val imageRepo: ImageRepo
) {

    @PostMapping("/upload")
    fun uploadImages(
        @RequestParam("files")
        files: List<MultipartFile>,
        authentication: Authentication
    ): ResponseEntity<Any> {
        return try {
            val authUser = authentication.toUser()

            val responseMessages = mutableListOf<String>()

            for (file in files) {
                val fileName = imageService.storeFile(file)

                // Save image metadata to the database (if needed)
                val image = Image(
                    name = fileName,
                    contentType = file.contentType ?: "",
                    size = file.size,
                    vehicle = authUser.vehicles.last()
                )
                imageRepo.save(image)

                responseMessages.add("File uploaded successfully: $fileName")
            }

            ResponseEntity.ok(responseMessages.joinToString("\n"))
        } catch (ex: Exception) {
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Failed to upload one or more files: ${ex.message}")
        }
    }

    @GetMapping("/preview/{fileName:.+}")
    fun previewImage(
        @PathVariable("fileName") fileName: String
    ): ResponseEntity<Resource> {
        // Load the file as a Resource
        val fileResource: Resource = imageService.loadFile(fileName)

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

    @GetMapping
    fun getImages(): ResponseEntity<Any>{
        val images = imageService.findAll()
        return ResponseEntity.ok(images)
    }

    @GetMapping("/{vehicleId}")
    fun displayImages(
        @PathVariable("vehicleId") id: Long)
    : ResponseEntity<Any>{
        val images = imageRepo.findByVehicleId(vehicleId = id)
            .orElseThrow { throw NotFoundException("Images not found.") }
        val imageDtos = mutableListOf<ImageDto>()
        for (image in images) {
            imageDtos.add(image.toDto())
        }
        return ResponseEntity.ok(imageDtos)
    }

    @GetMapping("/download/{fileName:.+}")
    fun downloadImage(
        @PathVariable("fileName") fileName: String
    ): ResponseEntity<Resource> {
        // Load the file as a Resource
        val fileResource: Resource = imageService.loadFile(fileName)

        // Define Content-Disposition header for download
        val contentDisposition = ContentDisposition
            .builder("inline")
            .filename(fileName)
            .build()

        // Return the file as a ResponseEntity
        return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_DISPOSITION, contentDisposition.toString())
            .contentType(MediaType.APPLICATION_OCTET_STREAM)
            .body(fileResource)
    }
}
