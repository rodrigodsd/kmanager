package com.finance.kmanager.portfolio.internal

import com.finance.kmanager.portfolio.PositionDto
import com.finance.kmanager.portfolio.domain.Position
import org.mapstruct.Mapper
import org.mapstruct.MappingConstants

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
interface PositionMapper {
    fun positionToPositionDto(position: Position): PositionDto
    fun positionDtoToPosition(positionDto: PositionDto): Position
}