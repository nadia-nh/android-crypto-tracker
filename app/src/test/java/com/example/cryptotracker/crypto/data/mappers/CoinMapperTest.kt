package com.example.cryptotracker.crypto.data.mappers

import com.example.cryptotracker.crypto.data.networking.dto.CoinDto
import org.junit.Assert.assertEquals
import org.junit.Test

class CoinMapperTest {

    @Test
    fun toCoinMapsAllFieldsCorrectly() {
        val dto = CoinDto(
            id = "bitcoin",
            rank = 1,
            name = "Bitcoin",
            symbol = "BTC",
            marketCapUsd = 1_000_000.0,
            priceUsd = 50_000.0,
            changePercent24Hr = 2.5
        )

        val coin = dto.toCoin()

        assertEquals("bitcoin", coin.id)
        assertEquals(1, coin.rank)
        assertEquals("Bitcoin", coin.name)
        assertEquals("BTC", coin.symbol)
        assertEquals(1_000_000.0, coin.marketCapUsd, 0.0)
        assertEquals(50_000.0, coin.priceUsd, 0.0)
        assertEquals(2.5, coin.changePercent24Hs, 0.0)
    }

    @Test
    fun toCoinMapsNegativeChangePercent() {
        val dto = CoinDto("eth", 2, "Ethereum", "ETH", 500.0, 1800.0, -3.14)
        assertEquals(-3.14, dto.toCoin().changePercent24Hs, 0.0)
    }

    @Test
    fun toCoinMapsZeroNumericValues() {
        val dto = CoinDto("test", 0, "Test", "TST", 0.0, 0.0, 0.0)
        val coin = dto.toCoin()
        assertEquals(0.0, coin.marketCapUsd, 0.0)
        assertEquals(0.0, coin.priceUsd, 0.0)
        assertEquals(0.0, coin.changePercent24Hs, 0.0)
    }

    @Test
    fun toCoinPreservesLargeNumericValuesWithoutPrecisionLoss() {
        val largeMarketCap = 1_234_567_890.123456
        val dto = CoinDto("big", 1, "Big", "BIG", largeMarketCap, 99999.99, 0.0)
        assertEquals(largeMarketCap, dto.toCoin().marketCapUsd, 0.0)
    }
}
