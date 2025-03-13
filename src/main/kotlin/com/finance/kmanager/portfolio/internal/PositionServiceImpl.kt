package com.finance.kmanager.portfolio.internal

import com.finance.kmanager.portfolio.PositionDto
import com.finance.kmanager.portfolio.domain.Position
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class PositionServiceImpl(private val positionRepository: PositionRepository,
                          private val positionMapper: PositionMapper
) : PositionService {

    @Transactional
    override fun save(position: Position): PositionDto {
        return positionRepository.save(position).let { positionMapper.positionToPositionDto(it) }
    }

    @Transactional
    override fun saveAll(positions: List<Position>): List<PositionDto> {
        return positionRepository.saveAll(positions).map { positionMapper.positionToPositionDto(it)}
    }

    override fun getAllByPortfolioId(id: Int): List<PositionDto> {
        return positionRepository.getAllByPortfolioId(id).map { positionMapper.positionToPositionDto(it) }
    }

}