package com.inhouse.yoursell.service

import com.inhouse.yoursell.dto.ImageDto
import com.inhouse.yoursell.dto.toDto
import com.inhouse.yoursell.exceptions.FileStorageException
import com.inhouse.yoursell.repo.ImageRepo
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.StandardCopyOption
import org.springframework.core.io.Resource
import org.springframework.core.io.UrlResource
import java.net.MalformedURLException
import java.nio.file.Paths

@Service
class ImageService(
    private val imageRepo: ImageRepo,

    @Value("\${file.upload-dir}")
    private val uploadDir: String
) {

    fun storeFile(file: MultipartFile): String {
        val fileName = "${System.currentTimeMillis()}_${file.originalFilename}"
        val targetLocation: Path = Path.of(uploadDir).resolve(fileName)

        try {
            Files.copy(file.inputStream,
                targetLocation,
                StandardCopyOption.REPLACE_EXISTING)
            return fileName
        } catch (ex: IOException) {
            throw FileStorageException("Could not store file $fileName. Please try again!", ex)
        }
    }

    fun loadFile(fileName: String): Resource {
        try {
            val filePath: Path = Paths.get(uploadDir).resolve(fileName)
            val resource: Resource = UrlResource(filePath.toUri())

            if (resource.exists() && resource.isReadable) {
                return resource
            } else {
                throw FileStorageException("File $fileName not found or is not readable")
            }
        } catch (ex: MalformedURLException) {
            throw FileStorageException("Malformed URL Exception for file: $fileName", ex)
        }
    }

    fun findAll(): MutableList<ImageDto> {
        val output = mutableListOf<ImageDto>()

        val images = imageRepo.findAll()

        for (image in images) {
            output.add(image.toDto())
        }
        return output
    }
}