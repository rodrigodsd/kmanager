package com.finance.kmanager

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class KmanagerApplication

fun main(args: Array<String>) {
	runApplication<KmanagerApplication>(*args)
}