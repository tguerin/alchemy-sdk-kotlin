package com.alchemy.sdk.wallet.connect.v1.impls

import com.alchemy.sdk.util.HexString.Companion.hexString
import org.bouncycastle.crypto.digests.SHA256Digest
import org.bouncycastle.crypto.engines.AESEngine
import org.bouncycastle.crypto.macs.HMac
import org.bouncycastle.crypto.modes.CBCBlockCipher
import org.bouncycastle.crypto.paddings.PKCS7Padding
import org.bouncycastle.crypto.paddings.PaddedBufferedBlockCipher
import org.bouncycastle.crypto.params.KeyParameter
import org.bouncycastle.crypto.params.ParametersWithIV
import com.alchemy.sdk.wallet.connect.v1.Session
import com.google.gson.Gson
import java.security.SecureRandom

class GsonEncryption(gson: Gson) : Session.PayloadEncryption {

    private val encryptedPayloadAdapter = gson.getAdapter(EncryptedPayload::class.java)

    override fun encrypt(unencryptedPayloadJson: String, key: String): String {
        val bytesData = unencryptedPayloadJson.toByteArray()
        val hexKey = key.hexString
        val iv = createRandomBytes(16)

        val padding = PKCS7Padding()
        val aes = PaddedBufferedBlockCipher(
            CBCBlockCipher(AESEngine()),
            padding
        )
        aes.init(true, ParametersWithIV(KeyParameter(hexKey.toByteArray()), iv))

        val minSize = aes.getOutputSize(bytesData.size)
        val outBuf = ByteArray(minSize)
        val length1 = aes.processBytes(bytesData, 0, bytesData.size, outBuf, 0)
        aes.doFinal(outBuf, length1)


        val hmac = HMac(SHA256Digest())
        hmac.init(KeyParameter(hexKey.toByteArray()))

        val hmacResult = ByteArray(hmac.macSize)
        hmac.update(outBuf, 0, outBuf.size)
        hmac.update(iv, 0, iv.size)
        hmac.doFinal(hmacResult, 0)

        return encryptedPayloadAdapter.toJson(
            EncryptedPayload(
                outBuf.hexString.withoutPrefix(),
                hmac = hmacResult.hexString.withoutPrefix(),
                iv = iv.hexString.withoutPrefix()
            )
        )
    }

    override fun decrypt(encryptedPayloadJson: String, key: String): String {
        val encryptedPayload = encryptedPayloadAdapter.fromJson(encryptedPayloadJson) ?: throw IllegalArgumentException("Invalid json payload!")

        val hexKey = key.hexString.toByteArray()
        val iv = encryptedPayload.iv.hexString.toByteArray()
        val encryptedData = encryptedPayload.data.hexString.toByteArray()
        val providedHmac = encryptedPayload.hmac.hexString.toByteArray()

        // verify hmac
        with(HMac(SHA256Digest())) {
            val hmacResult = ByteArray(macSize)
            init(KeyParameter(hexKey))
            update(encryptedData, 0, encryptedData.size)
            update(iv, 0, iv.size)
            doFinal(hmacResult, 0)

            require(hmacResult.contentEquals(providedHmac)) { "HMAC does not match - expected: $hmacResult received: $providedHmac" }
        }

        // decrypt payload
        val padding = PKCS7Padding()
        val aes = PaddedBufferedBlockCipher(
            CBCBlockCipher(AESEngine()),
            padding
        )
        val ivAndKey = ParametersWithIV(
            KeyParameter(hexKey),
            iv
        )
        aes.init(false, ivAndKey)

        val minSize = aes.getOutputSize(encryptedData.size)
        val outBuf = ByteArray(minSize)
        var len = aes.processBytes(encryptedData, 0, encryptedData.size, outBuf, 0)
        len += aes.doFinal(outBuf, len)

        return String(outBuf.copyOf(len))
    }

    private fun createRandomBytes(i: Int) = ByteArray(i).also { SecureRandom().nextBytes(it) }
}

data class EncryptedPayload(
    val data: String,
    val iv: String,
    val hmac: String
)