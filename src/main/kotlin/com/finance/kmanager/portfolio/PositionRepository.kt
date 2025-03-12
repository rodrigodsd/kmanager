package com.finance.kmanager.portfolio

import com.finance.kmanager.portfolio.domain.Position
import org.springframework.data.repository.ListCrudRepository
import org.springframework.stereotype.Repository

@Repository
interface PositionRepository : ListCrudRepository<Position, Long>{

}
