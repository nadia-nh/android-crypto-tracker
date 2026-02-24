package com.example.cryptotracker.crypto.data.mappers

import com.example.cryptotracker.crypto.data.networking.dto.CoinPriceDto
import com.example.cryptotracker.crypto.domain.CoinPrice
import java.time.Instant
import java.time.ZoneId
import java.time.ZonedDateTime

fun CoinPriceDto.toCoinPrice(): CoinPrice =
    CoinPrice(
        priceUsd = priceUsd,
        dateTime = ZonedDateTime.ofInstant(
            Instant.ofEpochMilli(time),
            ZoneId.of("UTC")
        )
    )
