package com.foodduck.foodduck

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.ComponentScan
import org.springframework.data.jpa.repository.config.EnableJpaAuditing

@SpringBootApplication
@EnableJpaAuditing
class FoodduckApplication

fun main(args: Array<String>) {
	runApplication<FoodduckApplication>(*args)
}
