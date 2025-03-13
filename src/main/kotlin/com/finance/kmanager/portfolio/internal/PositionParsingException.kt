package com.finance.kmanager.portfolio.internal

/**
 * Custom exception for handling Excel parsing errors.
 */
class PositionParsingException(message: String, cause: Throwable? = null) : RuntimeException(message, cause)