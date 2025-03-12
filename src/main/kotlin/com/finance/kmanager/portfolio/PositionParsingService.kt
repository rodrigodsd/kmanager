package com.finance.kmanager.portfolio

import com.finance.kmanager.portfolio.domain.Portfolio
import com.finance.kmanager.portfolio.domain.Position
import org.apache.poi.ss.usermodel.Cell
import org.apache.poi.ss.usermodel.Row
import java.math.BigDecimal
import java.util.*

// Abstract the parsing behavior into a generic interface
interface PositionParser {
    fun parse(row: Row, portfolio: Portfolio): Position
}

// Core utility functions for parsing data from Excel cells
object CellValueParser {

    fun getCellValueAsString(cell: Cell?): String {
        if (cell == null) return ""
        return when (cell.cellType) {
            org.apache.poi.ss.usermodel.CellType.STRING -> cell.richStringCellValue.string.trim()
            org.apache.poi.ss.usermodel.CellType.NUMERIC -> if (org.apache.poi.ss.usermodel.DateUtil.isCellDateFormatted(cell)) {
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

// Enum representing position mappers for different asset types
enum class PositionMapper(
    private val assetName: String,
    private val parser: PositionParser
) : PositionParser {

    ASSET("Acoes", VariableIncomeParser()),
    BDR("BDR", BDRParser()),
    ETF("ETF", VariableIncomeParser()),
    FII("Fundo de Investimento", VariableIncomeParser()),
    FIXED_INCOME("Renda Fixa", FixedIncomeParser()),
    TREASURIES("Tesouro Direto", TreasuriesParser());

    // Delegate parsing to the specific parser implementation
    override fun parse(row: Row, portfolio: Portfolio): Position {
        return parser.parse(row, portfolio)
    }

    companion object {
        fun findByName(name: String?): Optional<PositionMapper> {
            return values().find { it.assetName.equals(name, ignoreCase = true) }.let { Optional.ofNullable(it) }
        }
    }
}

// Parse logic for variable income (common for ASSET, ETF, FII)
class VariableIncomeParser : PositionParser {
    override fun parse(row: Row, portfolio: Portfolio): Position {
        val isin = CellValueParser.getCellValueAsString(row.getCell(5))
        return Position(
            id = null,
            portfolioId = portfolio.id!!,
            assetName = CellValueParser.getCellValueAsString(row.getCell(3)),
            isin = if (isin.isNotEmpty()) isin.take(12) else "",
            quantity = CellValueParser.getCellValueAsBigDecimal(row.getCell(8)),
            price = BigDecimal.ZERO
        )
    }
}

// Parse logic for BDR
class BDRParser : PositionParser {
    override fun parse(row: Row, portfolio: Portfolio): Position {
        val isin = CellValueParser.getCellValueAsString(row.getCell(4))
        return Position(
            id = null,
            portfolioId = portfolio.id!!,
            assetName = CellValueParser.getCellValueAsString(row.getCell(3)),
            isin = if (isin.isNotEmpty()) isin.take(12) else "",
            quantity = CellValueParser.getCellValueAsBigDecimal(row.getCell(8)),
            price = BigDecimal.ZERO
        )
    }
}

// Parse logic for Fixed Income
class FixedIncomeParser : PositionParser {
    override fun parse(row: Row, portfolio: Portfolio): Position {
        return Position(
            id = null,
            portfolioId = portfolio.id!!,
            assetName = CellValueParser.getCellValueAsString(row.getCell(3)),
            isin = "",
            quantity = CellValueParser.getCellValueAsBigDecimal(row.getCell(8)),
            price = BigDecimal.ZERO
        )
    }
}

// Parse logic for Treasuries
class TreasuriesParser : PositionParser {
    override fun parse(row: Row, portfolio: Portfolio): Position {
        return Position(
            id = null,
            portfolioId = portfolio.id!!,
            assetName = CellValueParser.getCellValueAsString(row.getCell(2)),
            isin = "",
            quantity = CellValueParser.getCellValueAsBigDecimal(row.getCell(5)),
            price = BigDecimal.ZERO
        )
    }
}