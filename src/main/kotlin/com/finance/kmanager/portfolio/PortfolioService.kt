package com.finance.kmanager.portfolio

import org.springframework.web.multipart.MultipartFile

interface PortfolioService {

    fun importPortfolio(file: MultipartFile, portfolioName: String, investorId: Int): HashMap<String, Int>
}