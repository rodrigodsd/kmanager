package com.finance.kmanager.portfolio.domain

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table
// TODO Add create_at and update_at
@Table("portfolio")
class Portfolio(@Id val id: Int?, val investorId: Int, val name: String, val description: String, val active: Boolean = true, ) {

    constructor(name: String, investorId: Int) : this(null, investorId, name, "")
}