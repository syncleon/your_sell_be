package com.inhouse.yoursell.service

import com.inhouse.yoursell.config.toUser
import com.inhouse.yoursell.dto.CreateItemDto
import com.inhouse.yoursell.dto.ItemDto
import com.inhouse.yoursell.dto.toDto
import com.inhouse.yoursell.entity.item.Item
import com.inhouse.yoursell.exceptions.BadRequestException
import com.inhouse.yoursell.exceptions.FileStorageException
import com.inhouse.yoursell.exceptions.NotFoundException
import com.inhouse.yoursell.repo.ItemRepo
import jakarta.transaction.Transactional
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.core.io.Resource
import org.springframework.core.io.UrlResource
import org.springframework.security.core.Authentication
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import java.io.IOException
import java.net.MalformedURLException
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.StandardCopyOption
import java.util.*

@Service
@Transactional
class ItemService(
    @Autowired private val itemRepo: ItemRepo,
    @Value("\${file.upload-dir}") private val uploadDir: String
) {
    fun findById(id: UUID): ItemDto {
        val item = itemRepo.findById(id).orElseThrow { throw NotFoundException("Item with $id not found!") }
        return item.toDto()
    }

    fun findAll(): MutableList<ItemDto> {
        val itemList = itemRepo.findAll().sortedByDescending { it.created }
        return itemList.map { it.toDto() }.toMutableList()
    }

    fun getImagesById(id: UUID): Map<String, MutableList<String>> {
        val item = findById(id)
        return mapOf(
            "featured" to item.featured,
            "exterior" to item.exterior,
            "interior" to item.interior,
            "mechanical" to item.mechanical,
            "other" to item.other
        )
    }

    fun getImagesByCategory(id: UUID, category: String): MutableList<String> {
        val item = findById(id)
        return when (category.lowercase()) {
            "featured" -> item.featured
            "exterior" -> item.exterior
            "interior" -> item.interior
            "mechanical" -> item.mechanical
            "other" -> item.other
            else -> mutableListOf()
        }
    }

    fun getImageByCategoryAndFileName(id: UUID, category: String, fileName: String): Resource {
        // Log inputs for debugging
        println("Fetching file: $fileName in category: $category for item: $id")

        // Validate fileName and category before proceeding
        if (fileName.isBlank() || category.isBlank()) {
            throw IllegalArgumentException("Category or file name cannot be empty.")
        }

        val images = getImagesByCategory(id, category)

        if (images.contains(fileName)) {
            return loadFile(
                fileName = fileName,
                itemId = id.toString(),
                category = category)
        } else {
            // Log when file is not found in the category
            println("File $fileName not found in category $category for item $id")
            throw NotFoundException("File $fileName not found")
        }
    }

    fun createItem(
        authentication: Authentication,
        payload: CreateItemDto,
        imagesFeatured: List<MultipartFile>,
        imagesExterior: List<MultipartFile> = emptyList(),
        imagesInterior: List<MultipartFile> = emptyList(),
        imagesMechanical: List<MultipartFile> = emptyList(),
        imagesOther: List<MultipartFile> = emptyList()
    ): ItemDto {
        // Check if the payload is null or invalid
        if (payload.make.isBlank() || payload.model.isBlank()) {
            throw IllegalArgumentException("Invalid item payload.")
        }

        // Handle authentication
        val authUser = authentication.toUser()

        // Ensure at least one featured image is provided
        if (imagesFeatured.isEmpty()) {
            throw IllegalArgumentException("At least one featured image must be uploaded.")
        }

        // Create the item
        val item = Item(
            user = authUser,
            make = payload.make,
            model = payload.model,
            mileage = payload.mileage,
            year = payload.year
        )

        // Save the item to the repository
        val savedItem = try {
            itemRepo.save(item)
        } catch (e: Exception) {
            throw RuntimeException("Failed to create item: ${e.message}", e)
        }

        val itemId = savedItem.id

        // Check the featured images limit
        if (imagesFeatured.size > 1) {
            throw BadRequestException("Only one featured image can be uploaded.")
        }

        // Store files and handle potential storage failures
        try {
            savedItem.imagesFeatured = storeFiles(imagesFeatured, itemId, "featured")
            savedItem.imagesExterior = storeFiles(imagesExterior, itemId, "exterior")
            savedItem.imagesInterior = storeFiles(imagesInterior, itemId, "interior")
            savedItem.imagesMechanical = storeFiles(imagesMechanical, itemId, "mechanical")
            savedItem.imagesOther = storeFiles(imagesOther, itemId, "other")
        } catch (e: Exception) {
            throw RuntimeException("Failed to store images: ${e.message}", e)
        }

        // Save the updated item and return its DTO
        return itemRepo.save(savedItem).toDto()
    }


    fun storeFiles(files: List<MultipartFile>, itemId: UUID, category: String): MutableList<String> {
        val fileNames = mutableListOf<String>()

        files.forEach { file ->
            // Check if the original filename is empty
            if (file.originalFilename.isNullOrBlank()) {
                // Skip storing the file and move to the next one
                return@forEach
            }

            val fileName = "${System.currentTimeMillis()}_${file.originalFilename}"
            val targetLocation = Path.of(uploadDir, itemId.toString(), category).resolve(fileName)

            try {
                Files.createDirectories(targetLocation.parent)
                Files.copy(file.inputStream, targetLocation, StandardCopyOption.REPLACE_EXISTING)
                fileNames.add(fileName)
            } catch (ex: IOException) {
                throw FileStorageException("Could not store file $fileName. Please try again!", ex)
            }
        }

        return fileNames
    }


    fun loadFile(fileName: String, itemId: String, category: String): Resource {
        try {
            // Include the category in the file path
            val filePath = Path.of(uploadDir, itemId, category).resolve(fileName)

            // Log the file path for debugging
            println("Loading file from path: $filePath")

            val resource = UrlResource(filePath.toUri())
            if (resource.exists() && resource.isReadable) {
                return resource
            } else {
                throw FileStorageException("File $fileName not found or is not readable at path: $filePath")
            }
        } catch (ex: MalformedURLException) {
            throw FileStorageException("Malformed URL Exception for file: $fileName", ex)
        }
    }
}
