package com.example.cryptotracker.crypto.data.mappers

import com.example.cryptotracker.crypto.data.networking.dto.CoinPriceDto
import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.ZoneId
import java.time.ZoneOffset

class CoinPriceMapperTest {

    @Test
    fun toCoinPriceMapesPriceUsdCorrectly() {
        val dto = CoinPriceDto(priceUsd = 45_000.0, time = 0L)
        assertEquals(45_000.0, dto.toCoinPrice().priceUsd, 0.0)
    }

    @Test
    fun toCoinPriceConvertsEpoch0To1970Jan01Utc() {
        val coinPrice = CoinPriceDto(priceUsd = 1.0, time = 0L).toCoinPrice()
        assertEquals(1970, coinPrice.dateTime.year)
        assertEquals(1, coinPrice.dateTime.monthValue)
        assertEquals(1, coinPrice.dateTime.dayOfMonth)
        assertEquals(0, coinPrice.dateTime.hour)
        assertEquals(0, coinPrice.dateTime.minute)
    }

    @Test
    fun toCoinPriceSetsTimezoneToUtc() {
        val coinPrice = CoinPriceDto(priceUsd = 1.0, time = System.currentTimeMillis()).toCoinPrice()
        assertEquals(ZoneId.of("UTC"), coinPrice.dateTime.zone)
    }

    @Test
    fun toCoinPriceConvertsKnownTimestampCorrectly() {
        // 2024-01-15T12:00:00Z = 1705320000000 ms
        val coinPrice = CoinPriceDto(priceUsd = 100.0, time = 1705320000000L).toCoinPrice()
        assertEquals(2024, coinPrice.dateTime.year)
        assertEquals(1, coinPrice.dateTime.monthValue)
        assertEquals(15, coinPrice.dateTime.dayOfMonth)
        assertEquals(12, coinPrice.dateTime.hour)
        assertEquals(ZoneOffset.UTC, coinPrice.dateTime.offset)
    }
}
