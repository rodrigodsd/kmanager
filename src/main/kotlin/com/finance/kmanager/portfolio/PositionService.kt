package com.finance.kmanager.portfolio

import com.finance.kmanager.portfolio.domain.Position

interface PositionService {
    fun save(position: Position): Position

    fun saveAll(positions: List<Position>): List<Position>
}
