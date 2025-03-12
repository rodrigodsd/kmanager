package com.finance.kmanager.portfolio

import org.apache.poi.ss.usermodel.Cell
import java.math.BigDecimal

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