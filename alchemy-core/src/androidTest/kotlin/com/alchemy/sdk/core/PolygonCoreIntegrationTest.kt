package com.alchemy.sdk.core

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.alchemy.sdk.core.model.AlchemySettings
import com.alchemy.sdk.core.model.Network
import com.alchemy.sdk.core.model.RawInt.Companion.raw
import com.alchemy.sdk.core.util.HexString.Companion.hexString
import kotlinx.coroutines.test.runTest
import org.amshove.kluent.shouldBeEqualTo
import org.amshove.kluent.shouldNotBe
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class PolygonCoreIntegrationTest {

    private val alchemy = Alchemy.with(AlchemySettings(network = Network.MATIC_MAINNET))

    @Test
    fun getAuthor() = runTest {
        val data = alchemy.core.getAuthor()
        data.getOrThrow().toString().length shouldNotBe 0
    }

    @Test
    fun getSignerAtHash() = runTest {
        // This method doesn't seem to be supported
        val data =
            alchemy.core.getSignersAtHash("0x350a56b14a8eb85de01d759794bce336689c40401f4b5b42192352dfcef16c35".hexString)
        data.isFailure shouldBeEqualTo true
    }

    @Test
    fun getCurrentProposer() = runTest {
        val data = alchemy.core.getCurrentProposer()
        data.getOrThrow().toString().length shouldNotBe 0
    }

    @Test
    fun getRootHash() = runTest {
        val data = alchemy.core.getRootHash(from = 1.raw, to = 2.raw)
        data.getOrThrow() shouldBeEqualTo "e105ee4b21c49a6f79a26647122abc8ad0b5aed34801999e30bc37ea5b0c589b".hexString
    }

    @Test
    fun getCurrentValidators() = runTest {
        val data = alchemy.core.getCurrentValidators()
        val validators = data.getOrThrow()
        // If we are here the parsing succeeded, hard to find values to compare with
        validators.size shouldNotBe 0
    }
}