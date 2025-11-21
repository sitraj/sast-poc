package com.sast.demo

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class SastDemoApplication

fun main(args: Array<String>) {
    runApplication<SastDemoApplication>(*args)
}
