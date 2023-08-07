package com.inhouse.yoursell.controller

import com.inhouse.yoursell.config.toUser
import com.inhouse.yoursell.dto.toDto
import com.inhouse.yoursell.exceptions.NotFoundException
import com.inhouse.yoursell.service.UserService
import org.springframework.http.ResponseEntity
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.*


@RestController
@RequestMapping("/api/v1/users")
class UserController(
    val userService: UserService
) {

    @GetMapping
    fun getUsers(): ResponseEntity<Any> {
        return try {
            ResponseEntity.ok(userService.findAll())
        } catch (e: Exception) {
            ResponseEntity.badRequest().body(e.message)
        }
    }

    @GetMapping("/{id}")
    fun getUser(
        authentication: Authentication,
        @PathVariable("id") id: Long
    ): ResponseEntity<Any> {
        return try {
            ResponseEntity.ok(userService.findById(id).toDto())
        } catch (e: NotFoundException) {
            ResponseEntity.badRequest().body(e.message)
        }
    }

    @GetMapping("/current")
    fun getCurrentUser(
        authentication: Authentication,
    ): ResponseEntity<Any> {
        return try {
            ResponseEntity.ok(authentication.toUser().toDto())
        } catch (e: NotFoundException) {
            ResponseEntity.badRequest().body(e.message)
        }
    }

    @DeleteMapping
    fun deleteUser(
        authentication: Authentication
    ): ResponseEntity<Any> {
        val user = authentication.toUser()
        return try {
            ResponseEntity.accepted().body(userService.delete(user))
        } catch (e: Exception) {
            ResponseEntity.badRequest().body(e.message)
        }
    }

}