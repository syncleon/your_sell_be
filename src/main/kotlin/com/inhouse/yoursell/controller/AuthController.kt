package com.inhouse.yoursell.controller

import com.inhouse.yoursell.dto.LoginResponseDto
import com.inhouse.yoursell.dto.LoginUserDto
import com.inhouse.yoursell.dto.RegisterUserDto
import com.inhouse.yoursell.entity.user.ERole
import com.inhouse.yoursell.entity.user.Role
import com.inhouse.yoursell.entity.user.User
import com.inhouse.yoursell.exceptions.AlreadyExistsException
import com.inhouse.yoursell.exceptions.NotFoundException
import com.inhouse.yoursell.repo.RoleRepo
import com.inhouse.yoursell.security.Hashing
import com.inhouse.yoursell.security.JwtTokenProvider
import com.inhouse.yoursell.service.UserService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/")
class AuthController(
    private val hashing: Hashing,
    private val jwtTokenProvider: JwtTokenProvider,
    private val userService: UserService,
    private val roleRepo: RoleRepo
    ) {

    @PostMapping("/signin")
    fun login(@RequestBody payload: LoginUserDto): ResponseEntity<Any> {
        return try {
            if (!userService.existsByName(payload.username)) {
                throw NotFoundException("Credentials wrong, check username or password.")
            }
            val user = userService.findByName(payload.username)
            if (!hashing.checkBcrypt(payload.password, user.password)) {
                throw NotFoundException("Credentials wrong, check username or password.")
            }
            ResponseEntity.ok().body(
                LoginResponseDto(
                    token = jwtTokenProvider.createToken(user)
                )
            )
        } catch (e: NotFoundException) {
            ResponseEntity.badRequest().body(e.message)
        }
    }

    @PostMapping("/signup")
    fun signup(@RequestBody payload: RegisterUserDto): ResponseEntity<Any> {
        val userRole = roleRepo.findByName(ERole.ROLE_USER)
        val roles = mutableSetOf<Role>()
        return try {
            if (userService.existsByName(payload.username)) {
                throw AlreadyExistsException("User with this name already exists.")
            }
            val user = User(
                username = payload.username,
                password = hashing.hashBcrypt(payload.password),
                email = payload.email
            )
            roles.add(userRole)
            user.roles = roles
            val savedUser = userService.save(user)

            ResponseEntity.ok().body(
                LoginResponseDto(
                    token = jwtTokenProvider.createToken(savedUser)
                )
            )
        } catch (e: Exception){
            ResponseEntity.badRequest().body(e.message)
        }
    }
}