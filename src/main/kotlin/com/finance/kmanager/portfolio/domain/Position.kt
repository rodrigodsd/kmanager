package com.finance.kmanager.portfolio.domain

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table
import java.math.BigDecimal

@Table("portfolio_position")
class Position(
    @Id val id: Int?,
    val portfolioId: Int,
    val code: String,
    val codeIsin: String,
    val quantity: BigDecimal,
    val priceAvarage: BigDecimal
) {
}