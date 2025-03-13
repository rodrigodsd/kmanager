package com.finance.kmanager.portfolio.internal

import com.finance.kmanager.portfolio.PositionDto
import com.finance.kmanager.portfolio.domain.Position

interface PositionService {
    fun save(position: Position): PositionDto
    fun saveAll(positions: List<Position>): List<PositionDto>
    fun getAllByPortfolioId(id: Int): List<PositionDto>
}
