package com.finance.kmanager.exception

/**
 * Custom exception for handling Excel parsing errors.
 */
class PositionParsingException(message: String, cause: Throwable? = null) : RuntimeException(message, cause)