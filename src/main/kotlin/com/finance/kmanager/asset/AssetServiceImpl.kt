package com.finance.kmanager.asset

import com.finance.kmanager.portfolio.PositionEventDto
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.context.event.EventListener
import org.springframework.modulith.events.ApplicationModuleListener
import org.springframework.stereotype.Service
import kotlin.reflect.KClass

@Service
class AssetServiceImpl {

    private val logger: Logger = LoggerFactory.getLogger(AssetServiceImpl::class.java)

    @EventListener(classes = [PositionEventDto::class])
    fun on(event: PositionEventDto) {
        logger.info("Starting creating Asset register for asset")
    }
}