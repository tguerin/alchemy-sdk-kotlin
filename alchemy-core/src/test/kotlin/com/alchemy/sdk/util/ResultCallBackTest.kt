package com.alchemy.sdk.util

import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.junit4.MockKRule
import io.mockk.verify
import org.amshove.kluent.shouldBeEqualTo
import org.junit.Rule
import org.junit.Test
import retrofit2.Call
import retrofit2.Callback
import retrofit2.HttpException
import retrofit2.Response
import java.io.IOException

internal class ResultCallBackTest {

    @get:Rule
    val mockkRule = MockKRule(this)

    @MockK
    lateinit var resultCall: ResultCall<String>

    @MockK
    lateinit var call: Call<String>

    @MockK
    lateinit var response: Response<String>

    @MockK
    lateinit var callback: Callback<Result<String>>

    @Test
    fun `returns a response success if original response is success`() {
        val resultCallBack = ResultCall.ResultCallback(resultCall, callback)

        every { response.isSuccessful } returns true
        every { response.code() } returns 200
        every { response.body() } returns "body"
        every { callback.onResponse(resultCall, any()) } returns Unit
        resultCallBack.onResponse(call, response)
        verify {
            callback.onResponse(
                resultCall,
                withArg {
                    it.isSuccessful shouldBeEqualTo true
                    it.code() shouldBeEqualTo 200
                    it.body() shouldBeEqualTo Result.success("body")
                }
            )
        }
    }

    @Test
    fun `returns a response success with result failure if original response is failure`() {
        val resultCallBack = ResultCall.ResultCallback(resultCall, callback)

        every { response.isSuccessful } returns false
        every { response.code() } returns 400
        every { response.message() } returns "message"
        every { callback.onResponse(resultCall, any()) } returns Unit
        resultCallBack.onResponse(call, response)
        verify {
            callback.onResponse(
                resultCall,
                withArg {
                    it.isSuccessful shouldBeEqualTo true
                    it.code() shouldBeEqualTo 200
                    it.body()?.isFailure shouldBeEqualTo true
                    it.body()?.exceptionOrNull()?.message shouldBeEqualTo "HTTP 400 message"
                }
            )
        }
    }

    @Test
    fun `returns a response success with result failure as no internet connection if original response is failure with IOException`() {
        val resultCallBack = ResultCall.ResultCallback(resultCall, callback)
        every { callback.onResponse(resultCall, any()) } returns Unit
        resultCallBack.onFailure(call, IOException("exception"))
        verify {
            callback.onResponse(
                resultCall,
                withArg {
                    it.body()?.exceptionOrNull()?.message shouldBeEqualTo  "No internet connection"
                }
            )
        }
    }

    @Test
    fun `returns a response success with forwarder exception if original response is failure`() {
        val resultCallBack = ResultCall.ResultCallback(resultCall, callback)

        every { callback.onResponse(resultCall, any()) } returns Unit
        resultCallBack.onFailure(call, RuntimeException("exception"))
        verify {
            callback.onResponse(
                resultCall,
                withArg {
                    it.body()?.exceptionOrNull()?.message shouldBeEqualTo  "exception"
                }
            )
        }
    }
}