package com.finance.kmanager.portfolio.internal

import com.finance.kmanager.portfolio.domain.Position
import org.springframework.web.multipart.MultipartFile

interface PortfolioService {

    fun importPortfolio(file: MultipartFile, portfolioName: String, investorId: Int): Map<String, Int>
    fun getPositions(id: Int): List<Position>
    fun updatePositions(positions: List<Position>): List<Position>
}