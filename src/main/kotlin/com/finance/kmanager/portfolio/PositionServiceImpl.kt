package com.finance.kmanager.portfolio

import com.finance.kmanager.portfolio.domain.Position
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class PositionServiceImpl(val positionRepository: PositionRepository) : PositionService {

    @Transactional
    override fun save(position: Position): Position {
        return positionRepository.save(position)
    }

    @Transactional
    override fun saveAll(positions: List<Position>): List<Position> {
        return positionRepository.saveAll(positions)
    }
}