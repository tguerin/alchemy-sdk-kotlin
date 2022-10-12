package com.alchemy.sdk.util

import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit4.MockKRule
import io.mockk.mockk
import io.mockk.verify
import okhttp3.Request
import okio.Timeout
import org.amshove.kluent.shouldBeEqualTo
import org.amshove.kluent.shouldNotBe
import org.junit.Rule
import org.junit.Test
import retrofit2.Call

internal class ResultCallTest {

    @get:Rule
    val mockkRule = MockKRule(this)

    @MockK
    lateinit var delegate: Call<String>

    @InjectMockKs
    lateinit var resultCall: ResultCall<String>


    @Test
    fun `should delegate isExecuted`() {
        every { delegate.isExecuted } returns true
        resultCall.isExecuted shouldBeEqualTo true
        verify { delegate.isExecuted }
    }

    @Test
    fun `should delegate cancel`() {
        every { delegate.cancel() } returns Unit
        resultCall.cancel()
        verify { delegate.cancel() }
    }

    @Test
    fun `should delegate isCanceled`() {
        every { delegate.isCanceled } returns true
        resultCall.isCanceled shouldBeEqualTo true
        verify { delegate.isCanceled }
    }

    @Test
    fun `should delegate clone`() {
        every { delegate.clone() } returns delegate
        resultCall.clone() shouldNotBe resultCall
        verify { delegate.clone() }
    }

    @Test
    fun `should delegate request`() {
        val request = mockk<Request>()
        every { delegate.request() } returns request
        resultCall.request() shouldBeEqualTo  request
        verify { delegate.request() }
    }

    @Test
    fun `should delegate timeout`() {
        val timeout = mockk<Timeout>()
        every { delegate.timeout() } returns timeout
        resultCall.timeout() shouldBeEqualTo  timeout
        verify { delegate.timeout() }
    }

}