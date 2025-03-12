package com.finance.kmanager.portfolio.domain

import com.finance.kmanager.portfolio.AssetType
import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table
import java.math.BigDecimal

@Table("asset")
class Asset(
    @Id val id: Int?,
    val type: AssetType,
    val code: String,
    val codeIsin: String,
    val description: String,
    val industry: String,
    val sector: String,
    val segment: String,
    val price: BigDecimal
) {

}
