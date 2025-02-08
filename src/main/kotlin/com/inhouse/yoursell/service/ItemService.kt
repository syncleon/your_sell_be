package com.inhouse.yoursell.service

import com.inhouse.yoursell.config.toUser
import com.inhouse.yoursell.dto.CreateItemDto
import com.inhouse.yoursell.dto.ItemDto
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
import toDto
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

    /**
     * Retrieves an item by its ID.
     */
    fun getItemById(id: UUID): ItemDto {
        val item = itemRepo.findById(id).orElseThrow { throw NotFoundException("Item with $id not found!") }
        return item.toDto()
    }

    /**
     * Retrieves all items, sorted by creation date.
     */
    fun getAllItems(): MutableList<ItemDto> {
        val itemList = itemRepo.findAll().sortedByDescending { it.created }
        return itemList.map { it.toDto() }.toMutableList()
    }

    /**
     * Retrieves all images for an item.
     */
    fun getItemImages(id: UUID): Map<String, List<String>> {
        val item = getItemById(id)
        return mapOf(
            "featured" to item.imagesFeatured,
            "exterior" to item.imagesExterior,
            "interior" to item.imagesInterior,
            "mechanical" to item.imagesMechanical,
            "other" to item.imagesOther
        )
    }

    /**
     * Retrieves images by category for an item.
     */
    fun getImagesByCategory(id: UUID, category: String): List<String> {
        val item = getItemById(id)
        return when (category.lowercase()) {
            "featured" -> item.imagesFeatured
            "exterior" -> item.imagesExterior
            "interior" -> item.imagesInterior
            "mechanical" -> item.imagesMechanical
            "other" -> item.imagesOther
            else -> mutableListOf()
        }
    }

    /**
     * Retrieves a specific image by category and filename for an item.
     */
    fun getImageByCategoryAndFileName(id: UUID, category: String, fileName: String): Resource {
        if (fileName.isBlank() || category.isBlank()) throw IllegalArgumentException("Category or file name cannot be empty.")

        val images = getImagesByCategory(id, category)
        if (images.contains(fileName)) {
            return loadFile(fileName = fileName, itemId = id.toString(), category = category)
        } else {
            throw NotFoundException("File $fileName not found")
        }
    }

    /**
     * Creates a new item with the provided details and images.
     */
    fun createNewItem(
        authentication: Authentication,
        payload: CreateItemDto,
        imagesFeatured: List<MultipartFile>,
        imagesExterior: List<MultipartFile> = emptyList(),
        imagesInterior: List<MultipartFile> = emptyList(),
        imagesMechanical: List<MultipartFile> = emptyList(),
        imagesOther: List<MultipartFile> = emptyList()
    ): ItemDto {
        validateItemData(payload, imagesFeatured)

        val authUser = authentication.toUser()

        val item = Item(
            user = authUser,
            make = payload.make,
            model = payload.model,
            mileage = payload.mileage,
            year = payload.year,
            price = payload.price,
            exteriorColor = payload.exteriorColor,
            interiorColor = payload.interiorColor,
            engine = payload.engineSize,
            fuelType = payload.fuelType,
            transmission = payload.transmission,
            bodyStyle = payload.bodyStyle,
            condition = payload.condition,
            drivetrain = payload.drivetrain,
            location = payload.location,
            description = payload.description,
            vin = payload.vin,
            onAuction = payload.onAuction,
            isSold = payload.isSold
        )

        val savedItem = itemRepo.save(item)
        try {
            savedItem.imagesFeatured = storeFiles(imagesFeatured, savedItem.id, "featured")
            savedItem.imagesExterior = storeFiles(imagesExterior, savedItem.id, "exterior")
            savedItem.imagesInterior = storeFiles(imagesInterior, savedItem.id, "interior")
            savedItem.imagesMechanical = storeFiles(imagesMechanical, savedItem.id, "mechanical")
            savedItem.imagesOther = storeFiles(imagesOther, savedItem.id, "other")
        } catch (e: Exception) {
            throw RuntimeException("Failed to store images: ${e.message}", e)
        }

        return itemRepo.save(savedItem).toDto()
    }

    /**
     * Stores image files in the appropriate directory.
     */
    fun storeFiles(files: List<MultipartFile>, itemId: UUID, category: String): MutableList<String> {
        val fileNames = mutableListOf<String>()
        files.forEach { file ->
            if (file.originalFilename.isNullOrBlank()) return@forEach

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

    /**
     * Loads a specific file from storage.
     */
    fun loadFile(fileName: String, itemId: String, category: String): Resource {
        try {
            val filePath = Path.of(uploadDir, itemId, category).resolve(fileName)
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

    /**
     * Deletes an item and its associated images from storage.
     */
    fun deleteItem(authentication: Authentication, id: UUID) {
        val item = itemRepo.findById(id).orElseThrow { throw NotFoundException("Item with $id not found!") }
        val authUser = authentication.toUser()
        if (item.user?.id != authUser.id) throw BadRequestException("You are not authorized to delete this item.")

        deleteImagesFromStorage(item)
        item.user?.items?.remove(item)
        itemRepo.delete(item)
    }

    /**
     * Deletes all images associated with an item from storage.
     */
    private fun deleteImagesFromStorage(item: Item) {
        val categories = listOf(
            item.imagesFeatured,
            item.imagesExterior,
            item.imagesInterior,
            item.imagesMechanical,
            item.imagesOther
        )

        categories.forEach { imageList ->
            imageList.forEach { imageName ->
                try {
                    val filePath = Path.of(uploadDir, item.id.toString(), getCategory(item, imageList)).resolve(imageName)
                    Files.deleteIfExists(filePath)
                } catch (ex: IOException) {
                    println("Error deleting file $imageName: ${ex.message}")
                }
            }
        }
    }

    /**
     * Determines the category of images for an item.
     */
    private fun getCategory(item: Item, images: MutableList<String>): String {
        return when (images) {
            item.imagesFeatured -> "featured"
            item.imagesExterior -> "exterior"
            item.imagesInterior -> "interior"
            item.imagesMechanical -> "mechanical"
            item.imagesOther -> "other"
            else -> throw IllegalArgumentException("Unknown image category")
        }
    }

    /**
     * Validates the item data before creating a new item.
     */
    private fun validateItemData(payload: CreateItemDto, imagesFeatured: List<MultipartFile>) {
        if (imagesFeatured.isNullOrEmpty()) throw BadRequestException("At least one featured image should be provided.")
        if (imagesFeatured.size > 1) throw BadRequestException("Only one featured image can be uploaded.")

        when {
            payload.make.isEmpty() -> throw BadRequestException("Make couldn't be empty.")
            payload.model.isEmpty() -> throw BadRequestException("Model couldn't be empty.")
            payload.year.isEmpty() -> throw BadRequestException("Year couldn't be empty.")
            payload.price <= 0 -> throw BadRequestException("Price must be greater than zero.")
        }
    }
}
