package com.example.cryptotracker.core.domain.util

import org.junit.Assert.*
import org.junit.Test

class ResultTest {

    @Test
    fun mapTransformsSuccessValue() {
        val result: Result<Int, NetworkError> = Result.Success(2)
        assertEquals(Result.Success(6), result.map { it * 3 })
    }

    @Test
    fun mapPassesErrorThroughUnchanged() {
        val result: Result<Int, NetworkError> = Result.Error(NetworkError.UNKNOWN_ERROR)
        assertEquals(Result.Error(NetworkError.UNKNOWN_ERROR), result.map { it * 3 })
    }

    @Test
    fun onSuccessInvokesCallbackWithValueForSuccess() {
        var received: Int? = null
        Result.Success(42).onSuccess { received = it }
        assertEquals(42, received)
    }

    @Test
    fun onSuccessDoesNotInvokeCallbackForError() {
        var called = false
        Result.Error<NetworkError>(NetworkError.UNKNOWN_ERROR).onSuccess { called = true }
        assertFalse(called)
    }

    @Test
    fun onSuccessReturnsTheOriginalResult() {
        val result: Result<Int, NetworkError> = Result.Success(5)
        assertSame(result, result.onSuccess { })
    }

    @Test
    fun onErrorInvokesCallbackWithErrorForError() {
        var received: NetworkError? = null
        Result.Error(NetworkError.SERVER_ERROR).onError { received = it }
        assertEquals(NetworkError.SERVER_ERROR, received)
    }

    @Test
    fun onErrorDoesNotInvokeCallbackForSuccess() {
        var called = false
        Result.Success(1).onError { called = true }
        assertFalse(called)
    }

    @Test
    fun onErrorReturnsTheOriginalResult() {
        val result: Result<Int, NetworkError> = Result.Error(NetworkError.SERVER_ERROR)
        assertSame(result, result.onError { })
    }

    @Test
    fun asEmptyDataResultConvertsSuccessToUnit() {
        assertEquals(Result.Success(Unit), Result.Success(42).asEmptyDataResult())
    }

    @Test
    fun asEmptyDataResultPreservesError() {
        val original: Result<Int, NetworkError> = Result.Error(NetworkError.NO_INTERNET_CONNECTION)
        assertEquals(Result.Error(NetworkError.NO_INTERNET_CONNECTION), original.asEmptyDataResult())
    }

    @Test
    fun chainedOnSuccessAndOnErrorOnlyCallMatchingHandler() {
        var successCalled = false
        var errorCalled = false

        Result.Success(1)
            .onSuccess { successCalled = true }
            .onError { errorCalled = true }

        assertTrue(successCalled)
        assertFalse(errorCalled)
    }
}
