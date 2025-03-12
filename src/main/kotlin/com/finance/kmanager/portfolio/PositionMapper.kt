package com.finance.kmanager.portfolio


import com.finance.kmanager.portfolio.domain.Portfolio
import com.finance.kmanager.portfolio.domain.Position
import org.apache.commons.lang3.StringUtils
import org.apache.poi.ss.usermodel.Cell
import org.apache.poi.ss.usermodel.CellType
import org.apache.poi.ss.usermodel.DateUtil
import org.apache.poi.ss.usermodel.Row
import java.math.BigDecimal
import java.util.*

enum class PositionMapper(private val assetName: String) {
    ASSET("Acoes"),
    BDR("BDR") {
        override fun parse(row: Row, portfolio: Portfolio): Position {
            val isin = getCellValueAsString(row.getCell(4))
            return Position(
                null,
                portfolio.id!!,
                getCellValueAsString(row.getCell(3)),
                if (StringUtils.isNoneEmpty(isin)) StringUtils.left(isin, 12) else "",
                getCellValueAsDouble(row.getCell(8)),
                BigDecimal.ZERO
            )
        }
    },
    ETF("ETF"),
    FII("Fundo de Investimento"),
    FIXED_INCOME("Renda Fixa") {
        override fun parse(row: Row, portfolio: Portfolio): Position {
            return Position(
                null,
                portfolio.id!!,
                getCellValueAsString(row.getCell(3)),
                "",
                getCellValueAsDouble(row.getCell(8)),
                BigDecimal.ZERO
            )
        }
    },
    TREASURIES("Tesouro Direto") {
        override fun parse(row: Row, portfolio: Portfolio): Position {
            return Position(
                null,
                portfolio.id!!,
                getCellValueAsString(row.getCell(2)),
                "",
                getCellValueAsDouble(row.getCell(5)),
                BigDecimal.ZERO
            )
        }
    };

    //mapper for variable income asset
    open fun parse(row: Row, portfolio: Portfolio): Position {
        val isin = getCellValueAsString(row.getCell(5))
        return Position(
            null,
            portfolio.id!!,
            getCellValueAsString(row.getCell(3)),
            if (StringUtils.isNoneEmpty(isin)) StringUtils.left(isin, 12) else "",
            getCellValueAsDouble(row.getCell(8)),
            BigDecimal.ZERO
        )
    }

    companion object {
        fun findByName(name: String?): Optional<PositionMapper> {
            return Arrays.stream(entries.toTypedArray())
                .filter { mapper: PositionMapper -> mapper.assetName.equals(name, ignoreCase = true) }.findFirst()
        }

        private fun getCellValueAsString(cell: Cell): String {

            return when (cell.cellType) {
                CellType.STRING -> cell.richStringCellValue.string.trim { it <= ' ' }
                CellType.NUMERIC -> if (DateUtil.isCellDateFormatted(cell)) {
                    cell.dateCellValue.toString() // Adjust date format if needed
                } else {
                    cell.numericCellValue.toString()
                }

                else -> ""
            }
        }

        private fun getCellValueAsDouble(cell: Cell): BigDecimal {

            return when (cell.cellType) {
                CellType.NUMERIC -> BigDecimal.valueOf(cell.numericCellValue)
                CellType.STRING -> BigDecimal.valueOf(cell.richStringCellValue.string.trim { it <= ' ' }.toDouble())
                else -> BigDecimal.ZERO
            }
        }
    }
}
