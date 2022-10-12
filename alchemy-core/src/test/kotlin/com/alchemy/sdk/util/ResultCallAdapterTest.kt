package com.alchemy.sdk.util

import com.google.gson.reflect.TypeToken
import io.mockk.impl.annotations.MockK
import io.mockk.junit4.MockKRule
import org.amshove.kluent.shouldBeEqualTo
import org.junit.Rule
import org.junit.Test
import retrofit2.Call
import retrofit2.Retrofit

class ResultCallAdapterTest {

    @get:Rule
    val mockkRule = MockKRule(this)

    @MockK
    lateinit var retrofit: Retrofit

    @Test
    fun `should return null if return type is not a Call`() {
        ResultCallAdapter.get(
            String::class.java,
            emptyArray(),
            retrofit
        ) shouldBeEqualTo null
    }

    @Test
    fun `should return null if return type is not a Parameterized type`() {
        ResultCallAdapter.get(
            Call::class.java,
            emptyArray(),
            retrofit
        ) shouldBeEqualTo null
    }

    @Test
    fun `should return null if upper bound is not a result`() {
        ResultCallAdapter.get(
            TypeToken.getParameterized(
                Call::class.java,
                String::class.java
            ).type,
            emptyArray(),
            retrofit
        ) shouldBeEqualTo null
    }


}