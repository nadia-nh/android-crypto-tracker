package com.example.cryptotracker.crypto.data.networking

import com.example.cryptotracker.core.data.networking.constructUrl
import com.example.cryptotracker.core.data.networking.safeCall
import com.example.cryptotracker.core.domain.util.NetworkError
import com.example.cryptotracker.core.domain.util.Result
import com.example.cryptotracker.core.domain.util.map
import com.example.cryptotracker.crypto.data.mappers.toCoin
import com.example.cryptotracker.crypto.data.networking.dto.CoinResponseDto
import com.example.cryptotracker.crypto.domain.Coin
import com.example.cryptotracker.crypto.domain.CoinDataSource
import io.ktor.client.HttpClient
import io.ktor.client.request.get

class RemoteCoinDataSource(
    private val httpClient: HttpClient
) : CoinDataSource {
    override suspend fun getCoins(): Result<List<Coin>, NetworkError> {
        return safeCall<CoinResponseDto> {
            httpClient.get(
                urlString = constructUrl(url = "/assets")
            )
        }.map { response ->
            response.data.map { it.toCoin() }
        }
    }
}
