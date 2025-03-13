package com.finance.kmanager.asset

import com.finance.kmanager.portfolio.PositionEventDto

interface AssetService {

    fun on(event: PositionEventDto)
}
