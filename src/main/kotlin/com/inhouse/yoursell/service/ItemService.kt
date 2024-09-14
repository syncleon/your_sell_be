package com.inhouse.yoursell.service

import com.inhouse.yoursell.config.toUser
import com.inhouse.yoursell.dto.CreateItemDto
import com.inhouse.yoursell.dto.ItemDto
import com.inhouse.yoursell.dto.toDto
import com.inhouse.yoursell.entity.item.Item
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
class ItemService (
    @Autowired private val itemRepo: ItemRepo,
    @Value("\${file.upload-dir}") private val uploadDir: String
) {
    fun findById(id: UUID): ItemDto {
        val item = itemRepo.findById(id).orElseThrow { throw NotFoundException("Item with $id not found!") }
        return item.toDto()
    }

    fun findAll(): MutableList<ItemDto> {
        val itemList = itemRepo.findAll()
            .sortedByDescending { it.created }
        val itemDtoList = mutableListOf<ItemDto>()
        itemList.forEach { item ->
            val itemDto = item.toDto()
            itemDtoList.add(itemDto)
        }
        return itemDtoList
    }


    fun createItem(
        authentication: Authentication,
        payload: CreateItemDto,
        images: MutableList<MultipartFile>
    ): ItemDto {
        val authUser = authentication.toUser()
        val item = Item(
            user = authUser,
            make = payload.make,
            model = payload.model,
            mileage = payload.mileage,
            year = payload.year
        )
        val savedItem = itemRepo.save(item)
        val itemId = savedItem.id
        val storedFiles = storeFiles(images, itemId)
        savedItem.images = storedFiles
        val updatedItem = itemRepo.save(savedItem)
        return updatedItem.toDto()
    }

    fun storeFiles(files: List<MultipartFile>, itemId: UUID): MutableList<String> {
        val fileNames = mutableListOf<String>()
        for (file in files) {
            val fileName = "${System.currentTimeMillis()}_${file.originalFilename}"
            val targetLocation: Path = Path.of(uploadDir, itemId.toString()).resolve(fileName)
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

    fun loadFile(fileName: String, itemId: String): Resource {
        try {
            val filePath: Path = Path.of(uploadDir, itemId).resolve(fileName)
            val resource: Resource = UrlResource(filePath.toUri())
            when {
                resource.exists() && resource.isReadable -> return resource
                else -> throw FileStorageException("File $fileName not found or is not readable")
            }
        } catch (ex: MalformedURLException) {
            throw FileStorageException("Malformed URL Exception for file: $fileName", ex)
        }
    }
}