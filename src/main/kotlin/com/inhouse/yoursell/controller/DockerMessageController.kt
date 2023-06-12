package com.inhouse.yoursell.controller

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/docker")
class DockerMessageController {

    @GetMapping("/messages")
    fun hello(): String {
        return "Hello from docker"
    }
}