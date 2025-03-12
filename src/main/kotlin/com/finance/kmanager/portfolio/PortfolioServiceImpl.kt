package com.finance.kmanager.portfolio;

import com.finance.kmanager.portfolio.domain.Portfolio
import com.finance.kmanager.portfolio.domain.Position
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import org.yaml.snakeyaml.util.Tuple
import java.io.IOException


@Service
class PortfolioServiceImpl(
    val portfolioRepository: PortfolioRepository,
    val positionService: PositionService
) : PortfolioService {

    val logger: Logger = LoggerFactory.getLogger(PortfolioServiceImpl::class.java)

    @Value("\${app.dir.import}")
    lateinit var importPath: String

    override fun importPortfolio(file: MultipartFile, portfolioName: String, investorId: Int): HashMap<String, Int> {

        // Create Portfolio
        val portfolio: Portfolio = portfolioRepository.save(Portfolio(name = portfolioName, investorId = investorId))

        // Parse file to Positions
        val tupleResult: Tuple<List<Position>, HashMap<String, Int>>
        try {
            file.inputStream.use { `is` ->
                tupleResult = ExcelParserUtil.parseExcelFile(`is`, portfolio)
                val positions: List<Position> = tupleResult._1()
                val results = positionService.saveAll(positions)
                logger.info("{}", results.size)
            }
        } catch (e: IOException) {
            logger.error("IOException occurred while processing file", e)
            throw IOException(e.message, e)
        }

        return tupleResult._2()
    }
}
