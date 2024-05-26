package com.inhouse.yoursell.controller

import com.inhouse.yoursell.dto.LoginResponseDto
import com.inhouse.yoursell.dto.LoginUserDto
import com.inhouse.yoursell.dto.RegisterUserDto
import com.inhouse.yoursell.entity.user.ERole
import com.inhouse.yoursell.entity.user.Role
import com.inhouse.yoursell.entity.user.User
import com.inhouse.yoursell.exceptions.*
import com.inhouse.yoursell.repo.RoleRepo
import com.inhouse.yoursell.security.Hashing
import com.inhouse.yoursell.security.JwtTokenProvider
import com.inhouse.yoursell.service.UserService
import org.springframework.http.HttpStatus
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
            if(payload.username.isEmpty() || payload.password.isEmpty()) {
                throw BadRequestException("Username or password cannot be empty.")
            }
            if (!userService.existsByName(payload.username)) {
                throw NotFoundException("User not found.")
            }
            val user = userService.findByName(payload.username)
            if (!hashing.checkBcrypt(payload.password, user.password)) {
                throw UnauthorizedException("Incorrect password.")
            }
            ResponseEntity.ok().body(
                LoginResponseDto(
                    token = jwtTokenProvider.createToken(user)
                )
            )
        } catch (e: BadRequestException) {
            ResponseEntity.badRequest().body(e.message)
        } catch (e: NotFoundException) {
            ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.message)
        } catch (e: UnauthorizedException) {
            ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.message)
        } catch (e: Exception) {
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected error occurred.")
        }
    }


    @PostMapping("/signup")
    fun signup(@RequestBody payload: RegisterUserDto): ResponseEntity<Any> {
        val userRole = roleRepo.findByName(ERole.ROLE_USER)
        val roles = mutableSetOf<Role>()
        return try {
            if (payload.username.isEmpty() || payload.password.isEmpty()) {
                throw BadRequestException("Username or password cannot be empty.")
            }
            if (userService.existsByName(payload.username)) {
                throw AlreadyExistsException("User with this name already exists.")
            }
            if (!userService.isValidEmail(payload.email)) {
                throw InvalidDataException("Invalid email format.")
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
        } catch (e: BadRequestException) {
            ResponseEntity.badRequest().body(e.message)
        } catch (e: AlreadyExistsException) {
            ResponseEntity.status(HttpStatus.CONFLICT).body(e.message)
        } catch (e: InvalidDataException) {
            ResponseEntity.badRequest().body(e.message)
        } catch (e: Exception) {
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected error occurred.")
        }
    }
}