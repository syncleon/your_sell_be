package com.inhouse.yoursell.service

import com.inhouse.yoursell.dto.UserDto
import com.inhouse.yoursell.dto.toDto
import com.inhouse.yoursell.entity.user.User
import com.inhouse.yoursell.exceptions.NotFoundException
import com.inhouse.yoursell.repo.UserRepo
import com.inhouse.yoursell.repo.VehicleRepo
import jakarta.transaction.Transactional
import org.springframework.stereotype.Service


@Service
@Transactional
class UserService(
    private val userRepo: UserRepo,
    private val vehicleRepo: VehicleRepo
) {
    fun findById(id: Long): User {
        val user = userRepo.findById(id)
        return if (!user.isEmpty) {
            user.get()
        } else throw NotFoundException("User not found.")
    }

    fun findAll(): MutableList<UserDto> {
        val userList = userRepo.findAll()
        val userDtoList: MutableList<UserDto> = mutableListOf()
        userList.forEach { user ->
            val userDto = user.toDto()
            userDtoList.add(userDto)
        }
        return userDtoList
    }

    fun findByName(name: String): User {
        return userRepo.findByUsername(name)
    }

    fun existsByName(name: String): Boolean {
        return userRepo.existsByUsername(name)
    }

    fun save(user: User): User {
        return userRepo.save(user)
    }

    fun delete(user: User) {
        return userRepo.delete(user)
    }
}