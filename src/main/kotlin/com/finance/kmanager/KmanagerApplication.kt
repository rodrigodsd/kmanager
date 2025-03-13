package com.finance.kmanager

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.modulith.Modulith

@Modulith
@SpringBootApplication
class KmanagerApplication

fun main(args: Array<String>) {
	runApplication<KmanagerApplication>(*args)
}