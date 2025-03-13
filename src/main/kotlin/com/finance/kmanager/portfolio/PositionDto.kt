package com.finance.kmanager.portfolio

import org.springframework.data.annotation.Id
import java.math.BigDecimal

class PositionDto(
    val id: Int?,
    val portfolioId: Int,
    val code: String,
    val codeIsin: String,
    val quantity: BigDecimal,
    val priceAvarage: BigDecimal
) {
}