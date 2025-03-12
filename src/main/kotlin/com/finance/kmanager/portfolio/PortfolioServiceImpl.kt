package com.finance.kmanager.portfolio

import com.finance.kmanager.exception.PortfolioImportException
import com.finance.kmanager.portfolio.domain.Portfolio
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import java.io.IOException

@Service
class PortfolioServiceImpl(
    private val portfolioRepository: PortfolioRepository,
    private val positionService: PositionService
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
    private fun processPositions(file: MultipartFile, portfolio: Portfolio): Map<String, Int> {
        logger.info("Parsing positions from the file: ${file.originalFilename}")

        file.inputStream.use { inputStream ->

            val (positions, countMap) = ExcelParserUtil.parseExcelFile(inputStream, portfolio)

            logger.info("Parsed ${positions.size} positions successfully from the file.")
            val savedPositionsCount = positionService.saveAll(positions)
            logger.info("Saved ${savedPositionsCount.size} positions to the database.")

            return countMap
        }
    }
}