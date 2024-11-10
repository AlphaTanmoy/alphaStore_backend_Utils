package com.alphaStore.Utils

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cloud.client.discovery.EnableDiscoveryClient

@SpringBootApplication(
	scanBasePackages = [
		"com.alphaStore.utils",
		"com.alphaStore.core",
	]
)
@EnableDiscoveryClient
class UtilsApplication

fun main(args: Array<String>) {
	runApplication<UtilsApplication>(*args)
}
