package com.finance.kmanager.user

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController("/v1/user")
class InvestorController {

    @GetMapping
    fun user(id: Long): Investor {
        return Investor(
            name = TODO(),
            id = TODO(),
            password = TODO()
        );
    }
}