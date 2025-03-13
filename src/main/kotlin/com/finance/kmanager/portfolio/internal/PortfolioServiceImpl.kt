package com.finance.kmanager.portfolio.internal

import com.finance.kmanager.portfolio.domain.Portfolio
import com.finance.kmanager.portfolio.domain.Position
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import java.io.IOException

@Service
class PortfolioServiceImpl(
    private val portfolioRepository: PortfolioRepository,
    private val positionService: PositionService,
    private val applicationEventPublisher: ApplicationEventPublisher,
    private val positionMapper: PositionMapper
) : PortfolioService {

    private val logger: Logger = LoggerFactory.getLogger(PortfolioServiceImpl::class.java)

    @Value("\${app.dir.import}")
    lateinit var importPath: String

    override fun importPortfolio(file: MultipartFile, portfolioName: String, investorId: Int): Map<String, Int> {
        logger.info("Starting portfolio import for investorId: $investorId and portfolioName: $portfolioName")

        // Validate the input file
        validateFile(file)

        // Create Portfolio
        val portfolio = createPortfolio(portfolioName, investorId)

        // Process positions from the file
        return try {
            processPositions(file, portfolio)
        } catch (e: IOException) {
            logger.error("IO Exception occurred while processing the portfolio import.", e)
            throw PortfolioImportException("Failed to import portfolio due to a file processing error.", e)
        } catch (e: Exception) {
            logger.error("Unexpected error occurred during portfolio import.", e)
            throw PortfolioImportException("An unexpected error occurred while importing the portfolio.", e)
        }
    }

    override fun getPositions(id: Int): List<Position> {
        return positionService.getAllByPortfolioId(id);
    }

    override fun updatePositions(positions: List<Position>): List<Position> {
        return positionService.saveAll(positions);
    }

    /**
     * Validates the input file for null or empty conditions.
     */
    private fun validateFile(file: MultipartFile) {
        if (file.isEmpty) {
            logger.error("Uploaded file is empty.")
            throw PortfolioImportException("The uploaded file is empty. Please provide a valid file for portfolio import.")
        }
        logger.info("Validated the input file: ${file.originalFilename}")
    }

    /**
     * Creates and saves a portfolio in the repository.
     */
    private fun createPortfolio(portfolioName: String, investorId: Int): Portfolio {
        logger.info("Creating a new portfolio for investorId: $investorId with name: $portfolioName")
        return portfolioRepository.save(Portfolio(name = portfolioName, investorId = investorId))
    }

    /**
     * Processes the positions by parsing the file and saving them.
     */
    private fun processPositions(file: MultipartFile, portfolio: Portfolio): Map<String, Int>  {
        logger.info("Parsing positions from the file: ${file.originalFilename}")

        file.inputStream.use { inputStream ->

            val (positions, countMap) = ExcelParserUtil.parseExcelFile(inputStream, portfolio)

            // Aggregate by code sum by quantity
            val positionsAggregated = positions.groupBy { it.code }.map {
                Position(
                    null,
                    it.value.first().portfolioId,
                    it.value.first().code,
                    it.value.first().codeIsin,
                    it.value.sumOf { it.quantity },
                    it.value.first().priceAvarage
                )
            }

            logger.info("Parsed ${positions.size} positions successfully from the file.")
            val savedPositionsCount = positionService.saveAll(positionsAggregated)
            logger.info("Saved ${savedPositionsCount.size} positions to the database.")

            publishPositionDtos(positions)

            return countMap
        }
    }

    private fun publishPositionDtos(positions: List<Position>) = runBlocking {
        launch {
            val positionDtoList = positions.map { PositionMapper::positionToPositionDto }
            applicationEventPublisher.publishEvent(positionDtoList)
        }
    }
}