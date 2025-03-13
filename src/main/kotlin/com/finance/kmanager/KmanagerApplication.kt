package com.finance.kmanager

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.modulith.Modulith
import org.springframework.scheduling.annotation.EnableAsync
import org.springframework.transaction.annotation.EnableTransactionManagement

@EnableAsync
@EnableTransactionManagement
@SpringBootApplication
class KmanagerApplication

fun main(args: Array<String>) {
	runApplication<KmanagerApplication>(*args)
}