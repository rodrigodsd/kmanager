package com.finance.kmanager.portfolio;

import com.finance.kmanager.portfolio.domain.Portfolio
import org.springframework.data.repository.ListCrudRepository
import org.springframework.stereotype.Repository

@Repository
interface PortfolioRepository : ListCrudRepository<Portfolio, Long> {
}
