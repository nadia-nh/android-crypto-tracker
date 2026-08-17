package com.example.cryptotracker.core.data.networking

import com.example.cryptotracker.core.domain.util.NetworkError
import com.example.cryptotracker.core.domain.util.Result
import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.client.request.get
import io.ktor.client.statement.HttpResponse
import io.ktor.http.HttpStatusCode
import io.ktor.util.network.UnresolvedAddressException
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.SerializationException
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class SafeCallTest {

    // safeCall — exception handling

    @Test
    fun safeCallReturnsNoInternetConnectionForUnresolvedAddressException() = runTest {
        val result = safeCall<Unit> { throw UnresolvedAddressException() }
        assertEquals(Result.Error(NetworkError.NO_INTERNET_CONNECTION), result)
    }

    @Test
    fun safeCallReturnsSerializationErrorForSerializationException() = runTest {
        val result = safeCall<Unit> { throw SerializationException("bad json") }
        assertEquals(Result.Error(NetworkError.SERIALIZATION_ERROR), result)
    }

    @Test
    fun safeCallReturnsUnknownErrorForGenericException() = runTest {
        val result = safeCall<Unit> { throw RuntimeException("unexpected") }
        assertEquals(Result.Error(NetworkError.UNKNOWN_ERROR), result)
    }

    // respondToResult — HTTP status code mapping

    @Test
    fun respondToResultMaps200ToSuccess() = runTest {
        val result = respondToResult<ByteArray>(fakeResponse(200))
        assertTrue(result is Result.Success)
    }

    @Test
    fun respondToResultMaps299ToSuccess() = runTest {
        val result = respondToResult<ByteArray>(fakeResponse(299))
        assertTrue(result is Result.Success)
    }

    @Test
    fun respondToResultMaps408ToRequestTimeout() = runTest {
        assertEquals(Result.Error(NetworkError.REQUEST_TIMEOUT), respondToResult<Unit>(fakeResponse(408)))
    }

    @Test
    fun respondToResultMaps429ToTooManyRequests() = runTest {
        assertEquals(Result.Error(NetworkError.TOO_MANY_REQUESTS), respondToResult<Unit>(fakeResponse(429)))
    }

    @Test
    fun respondToResultMaps500ToServerError() = runTest {
        assertEquals(Result.Error(NetworkError.SERVER_ERROR), respondToResult<Unit>(fakeResponse(500)))
    }

    @Test
    fun respondToResultMaps503ToServerError() = runTest {
        assertEquals(Result.Error(NetworkError.SERVER_ERROR), respondToResult<Unit>(fakeResponse(503)))
    }

    @Test
    fun respondToResultMaps404ToUnknownError() = runTest {
        assertEquals(Result.Error(NetworkError.UNKNOWN_ERROR), respondToResult<Unit>(fakeResponse(404)))
    }

    @Test
    fun respondToResultMaps301ToUnknownError() = runTest {
        assertEquals(Result.Error(NetworkError.UNKNOWN_ERROR), respondToResult<Unit>(fakeResponse(301)))
    }

    private suspend fun fakeResponse(statusCode: Int): HttpResponse {
        val engine = MockEngine { respond("", HttpStatusCode.fromValue(statusCode)) }
        return HttpClient(engine).get("https://example.com")
    }
}
