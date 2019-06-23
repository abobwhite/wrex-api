package com.daugherty.wrex

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.scheduling.annotation.EnableAsync

@SpringBootApplication
@EnableAsync
class Application {

	static void main(String[] args) {
		SpringApplication.run(Application, args)
	}

}
