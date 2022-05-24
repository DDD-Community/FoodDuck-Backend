package com.foodduck.foodduck

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class FoodduckApplication

fun main(args: Array<String>) {
	runApplication<FoodduckApplication>(*args)
}
