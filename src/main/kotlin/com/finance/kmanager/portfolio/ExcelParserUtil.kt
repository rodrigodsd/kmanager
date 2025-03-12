package com.finance.kmanager.portfolio

import com.finance.kmanager.exception.PositionParsingException
import com.finance.kmanager.portfolio.domain.Portfolio
import com.finance.kmanager.portfolio.domain.Position
import org.apache.commons.lang3.StringUtils
import org.apache.poi.ss.usermodel.*
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.yaml.snakeyaml.util.Tuple
import java.io.InputStream

object ExcelParserUtil {
    private val logger: Logger = LoggerFactory.getLogger(ExcelParserUtil::class.java)

    /**
     * Main method for parsing an Excel file and extracting positions per sheet.
     */
    @Throws(PositionParsingException::class)
    fun parseExcelFile(inputStream: InputStream, portfolio: Portfolio): Tuple<List<Position>, Map<String, Int>> {
        val startTime = System.currentTimeMillis()
        val positions = mutableListOf<Position>()
        val counter = mutableMapOf<String, Int>()

        try {
            WorkbookFactory.create(inputStream).use { workbook ->
                workbook.forEach { sheet ->
                    try {
                        processSheet(sheet, portfolio, positions, counter)
                    } catch (e: Exception) {
                        logger.error("Error processing sheet: ${sheet.sheetName}", e)
                    }
                }
            }
        } catch (e: Exception) {
            logger.error("Error parsing Excel file", e)
            throw PositionParsingException("Failed to parse Excel file", e)
        }

        logger.info(
            "method=parseExcelFile, module=asset, portfolio={}, elapsed_time={}ms",
            portfolio.id,
            System.currentTimeMillis() - startTime
        )

        return Tuple(positions, counter)
    }

    /**
     * Process a single sheet to extract positions using the appropriate PositionMapper.
     */
    private fun processSheet(
        sheet: Sheet,
        portfolio: Portfolio,
        positions: MutableList<Position>,
        counter: MutableMap<String, Int>
    ) {
        val sheetName = sheet.sheetName
        try {
            PositionMapper.findByName(sheetName).ifPresentOrElse({ mapper ->
                val rowCount = parseRows(sheet, mapper, portfolio, positions)
                counter[sheetName] = rowCount
            }, {
                logger.info("Sheet '{}' is not supported and will be skipped", sheetName)
            })
        } catch (e: Exception) {
            logger.warn("Error while finding mapper for sheet '{}'", sheetName, e)
        }
    }

    /**
     * Parse rows of a sheet and return the count of processed rows.
     */
    private fun parseRows(
        sheet: Sheet,
        mapper: PositionMapper,
        portfolio: Portfolio,
        positions: MutableList<Position>
    ): Int {
        var rowCount = 0

        sheet.forEach { row ->
            try {
                if (isHeaderOrEmptyRow(row)) return@forEach

                val position = mapper.parse(row, portfolio)
                positions.add(position)
                rowCount++
            } catch (e: Exception) {
                logger.error("Error parsing row {}", row.rowNum, e)
            }
        }

        return rowCount
    }

    /**
     * Check if the row is a header or empty.
     */
    private fun isHeaderOrEmptyRow(row: Row): Boolean {
        return row.rowNum == 0 || row.getCell(0)?.stringCellValue?.let { StringUtils.isBlank(it) } == true
    }
}

