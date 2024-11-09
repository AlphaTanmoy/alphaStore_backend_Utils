package com.AlphaStore.Utils

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cloud.client.discovery.EnableDiscoveryClient

@SpringBootApplication
@EnableDiscoveryClient
class UtilsApplication

fun main(args: Array<String>) {
    runApplication<UtilsApplication>(*args)
}
