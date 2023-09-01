package com.inhouse.yoursell.service

import com.inhouse.yoursell.config.toUser
import com.inhouse.yoursell.dto.RegisterVehicleDto
import com.inhouse.yoursell.dto.VehicleDto
import com.inhouse.yoursell.dto.toDto
import com.inhouse.yoursell.entity.user.User
import com.inhouse.yoursell.entity.vehicle.Vehicle
import com.inhouse.yoursell.exceptions.FileStorageException
import com.inhouse.yoursell.exceptions.NotFoundException
import com.inhouse.yoursell.repo.VehicleRepo
import jakarta.transaction.Transactional
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
import java.nio.file.Paths
import java.nio.file.StandardCopyOption

@Service
@Transactional
class VehicleService (
    private val vehicleRepo: VehicleRepo,
    @Value("\${file.upload-dir}")
    private val uploadDir: String

) {

    fun findById(id: Long): VehicleDto {
        val vehicle = vehicleRepo.findById(id).orElseThrow {
            throw NotFoundException("Vehicle $id not found!")
        }
        return vehicle.toDto()
    }

    fun findAll(): MutableList<VehicleDto> {
        val vehicleList = vehicleRepo.findAll()
        val vehicleDtoList = mutableListOf<VehicleDto>()
        vehicleList.forEach { vehicle ->
            val vehicleDto = vehicle.toDto()
            vehicleDtoList.add(vehicleDto)
        }
        return vehicleDtoList
    }

    fun findBySeller(user: User): List<Vehicle> {
        return vehicleRepo.findBySeller(user)
    }

    fun createVehicle(
        authentication: Authentication,
        payload: RegisterVehicleDto,
        images: MutableList<MultipartFile>
    ): VehicleDto {
        val authUser = authentication.toUser()
        val vehicle = Vehicle(
            seller = authUser,
            make = payload.make,
            model = payload.model,
            mileage = payload.mileage,
            vin = payload.vin,
            year = payload.year,
            expectedBid = payload.expectedBid,
            images = storeFiles(images)
        )
        return vehicleRepo.save(vehicle).toDto()
    }

    fun storeFiles(files: List<MultipartFile>): MutableList<String> {
        val fileNames = mutableListOf<String>()

        for (file in files) {
            val fileName = "${System.currentTimeMillis()}_${file.originalFilename}"
            val targetLocation: Path = Path.of(uploadDir).resolve(fileName)

            try {
                Files.copy(file.inputStream, targetLocation, StandardCopyOption.REPLACE_EXISTING)
                fileNames.add(fileName)
            } catch (ex: IOException) {
                throw FileStorageException("Could not store file $fileName. Please try again!", ex)
            }
        }

        return fileNames
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

    fun softDeleteVehicle(authentication: Authentication, id: Long) {
        val authUser = authentication.toUser()
        val vehicle = vehicleRepo.findByIdAndSeller(id, authUser).orElseThrow {
            throw NotFoundException("Vehicle $id not found!")
        }
        vehicle.deleted = true
        vehicleRepo.save(vehicle)
    }
}