package com.daugherty.wrex

import com.github.cloudyrock.spring.v5.EnableMongock
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.scheduling.annotation.EnableAsync
import org.springframework.scheduling.annotation.EnableScheduling

@SpringBootApplication
@EnableAsync
@EnableScheduling
@EnableMongock
class Application {

	static void main(String[] args) {
		SpringApplication.run(Application, args)
	}

}
