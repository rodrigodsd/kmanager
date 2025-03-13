package com.finance.kmanager.portfolio.internal

import com.finance.kmanager.portfolio.domain.Portfolio
import com.finance.kmanager.portfolio.domain.Position
import org.apache.commons.lang3.StringUtils
import org.apache.poi.ss.usermodel.*
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.io.InputStream
import java.math.BigDecimal

object ExcelParserUtil {
    private val logger: Logger = LoggerFactory.getLogger(ExcelParserUtil::class.java)

    /**
     * Main method for parsing an Excel file and extracting positions per sheet.
     */
    @Throws(PositionParsingException::class)
    fun parseExcelFile(inputStream: InputStream, portfolio: Portfolio): Pair<List<Position>, Map<String, Int>> {
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

        return Pair(positions, counter)
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
            AssetMapper.findByName(sheetName).ifPresentOrElse({ mapper ->
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
        mapper: AssetMapper,
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

object CellValueParserUtil {

    fun getCellValueAsString(cell: Cell?): String {
        if (cell == null) return ""
        return when (cell.cellType) {
            org.apache.poi.ss.usermodel.CellType.STRING -> cell.richStringCellValue.string.trim()
            org.apache.poi.ss.usermodel.CellType.NUMERIC -> if (org.apache.poi.ss.usermodel.DateUtil.isCellDateFormatted(
                    cell
                )
            ) {
                cell.dateCellValue.toString() // Adjust date format if required
            } else {
                cell.numericCellValue.toString()
            }

            else -> ""
        }
    }

    fun getCellValueAsBigDecimal(cell: Cell?): BigDecimal {
        if (cell == null) return BigDecimal.ZERO
        return when (cell.cellType) {
            org.apache.poi.ss.usermodel.CellType.NUMERIC -> BigDecimal.valueOf(cell.numericCellValue)
            org.apache.poi.ss.usermodel.CellType.STRING -> {
                cell.richStringCellValue.string.trim().toBigDecimalOrNull() ?: BigDecimal.ZERO
            }

            else -> BigDecimal.ZERO
        }
    }
}

