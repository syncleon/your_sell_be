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

    fun getImagesById(id: UUID): Map<String, List<String>> {
        val item = findById(id)
        return mapOf(
            "featured" to item.featured,
            "exterior" to item.exterior,
            "interior" to item.interior,
            "mechanical" to item.mechanical,
            "other" to item.other
        )
    }

    fun getImagesByCategory(id: UUID, category: String): List<String> {
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
        if (fileName.isBlank() || category.isBlank()) throw IllegalArgumentException("Category or file name cannot be empty.")

        val images = getImagesByCategory(id, category)
        if (images.contains(fileName)) {
            return loadFile(fileName = fileName, itemId = id.toString(), category = category)
        } else {
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
        if (imagesFeatured.isNullOrEmpty()) throw BadRequestException("At least one featured image should be provided.")
        if (imagesFeatured.size > 1) throw BadRequestException("Only one featured image can be uploaded.")

        val authUser = authentication.toUser()

        when {
            payload.make.isEmpty() -> throw BadRequestException("Make couldn't be empty.")
            payload.model.isEmpty() -> throw BadRequestException("Model couldn't be empty.")
            payload.year.isEmpty() -> throw BadRequestException("Year couldn't be empty.")
            payload.mileage.isEmpty() -> throw BadRequestException("Mileage couldn't be empty.")
            payload.price <= 0 -> throw BadRequestException("Price must be greater than zero.")
            payload.vin.isEmpty() -> throw BadRequestException("VIN couldn't be empty.")
        }

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

        val itemId = savedItem.id
        try {
            savedItem.imagesFeatured = storeFiles(imagesFeatured, itemId, "featured")
            savedItem.imagesExterior = storeFiles(imagesExterior, itemId, "exterior")
            savedItem.imagesInterior = storeFiles(imagesInterior, itemId, "interior")
            savedItem.imagesMechanical = storeFiles(imagesMechanical, itemId, "mechanical")
            savedItem.imagesOther = storeFiles(imagesOther, itemId, "other")
        } catch (e: Exception) {
            throw RuntimeException("Failed to store images: ${e.message}", e)
        }

        return itemRepo.save(savedItem).toDto()
    }

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

    fun deleteItem(authentication: Authentication, id: UUID) {
        val item = itemRepo.findById(id).orElseThrow { throw NotFoundException("Item with $id not found!") }
        val authUser = authentication.toUser()
        if (item.user.id != authUser.id) throw BadRequestException("You are not authorized to delete this item.")

        deleteImagesFromStorage(item)
        item.user.items.remove(item)
        itemRepo.delete(item)
    }

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
}
