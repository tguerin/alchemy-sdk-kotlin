package com.alchemy.sdk.e2e

import com.alchemy.sdk.Alchemy
import com.alchemy.sdk.AlchemySettings
import com.alchemy.sdk.core.model.Network
import com.alchemy.sdk.core.model.RawInt.Companion.raw
import com.alchemy.sdk.util.HexString.Companion.hexString
import kotlinx.coroutines.test.runTest
import org.amshove.kluent.shouldBeEqualTo
import org.amshove.kluent.shouldNotBe
import org.junit.Test

class PolygonCoreIntegrationTest {

    private val alchemy = Alchemy.with(
        AlchemySettings(
            apiKey = System.getenv("ALCHEMY_API_TOKEN"),
            network = Network.MATIC_MAINNET
        )
    )

    @Test
    fun `retrieve author`() = runTest {
        val data = alchemy.core.getAuthor()
        data.getOrThrow().toString().length shouldNotBe 0
    }

    @Test
    fun `retrieve signer at a specific hash`() = runTest {
        // This method doesn't seem to be supported
        val data =
            alchemy.core.getSignersAtHash("0x350a56b14a8eb85de01d759794bce336689c40401f4b5b42192352dfcef16c35".hexString)
        data.isFailure shouldBeEqualTo true
    }

    @Test
    fun `retrieve the current proposer`() = runTest {
        val data = alchemy.core.getCurrentProposer()
        data.getOrThrow().toString().length shouldNotBe 0
    }

    @Test
    fun `retrieve the root hash`() = runTest {
        val data = alchemy.core.getRootHash(from = 1.raw, to = 2.raw)
        data.getOrThrow() shouldBeEqualTo "e105ee4b21c49a6f79a26647122abc8ad0b5aed34801999e30bc37ea5b0c589b".hexString
    }

    @Test
    fun `retrieve the current validators`() = runTest {
        val data = alchemy.core.getCurrentValidators()
        val validators = data.getOrThrow()
        // If we are here the parsing succeeded, hard to find values to compare with
        validators.size shouldNotBe 0
    }
}