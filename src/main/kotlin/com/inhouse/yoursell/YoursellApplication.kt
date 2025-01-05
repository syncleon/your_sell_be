package com.inhouse.yoursell

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.scheduling.annotation.EnableScheduling

@SpringBootApplication
@EnableScheduling
class YoursellApplication

fun main(args: Array<String>) {
	runApplication<YoursellApplication>(*args)
}
