package com.finance.kmanager.portfolio

import org.springframework.web.multipart.MultipartFile

interface PortfolioService {

    fun importPortfolio(file: MultipartFile, portfolioName: String, investorId: Int): Map<String, Int>
    fun getPositions(id: Int): List<PositionDto>
    fun updatePositions(positions: List<PositionDto>): List<PositionDto>
}