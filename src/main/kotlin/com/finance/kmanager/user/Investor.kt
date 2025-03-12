package com.finance.kmanager.user

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table

@Table("investor")
class Investor(@Id val id: Long, val name: String, val password: String) {

}

