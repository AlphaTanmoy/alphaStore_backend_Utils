package com.AlphaStore.Utils;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class UtilsApplication {

	public static void main(String[] args) {
		SpringApplication.run(UtilsApplication.class, args);
	}

}
