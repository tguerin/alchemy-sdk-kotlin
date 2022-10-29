package com.alchemy.sdk.tipsmydev.inject

import android.content.Context
import com.alchemy.sdk.Alchemy
import com.alchemy.sdk.AlchemySettings
import com.alchemy.sdk.core.model.Network
import com.alchemy.sdk.wallet.Wallet
import com.alchemy.sdk.wallet.connect.v1.Session
import com.alchemy.sdk.wallet.connect.v1.WalletConnectV1
import com.alchemy.sdk.wallet.connect.v1.impls.FileWCSessionStore
import com.alchemy.sdk.wallet.connect.v1.impls.GsonEncryption
import com.alchemy.sdk.wallet.connect.v1.impls.GsonPayloadAdapter
import com.alchemy.sdk.wallet.connect.v1.impls.OkHttpTransport
import com.google.gson.Gson
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import java.io.File
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Singleton
    @Provides
    fun provideAlchemy() = Alchemy.with(AlchemySettings(network = Network.ETH_GOERLI))

    @Singleton
    @Provides
    fun provideWallet(
        alchemy: Alchemy,
        @ApplicationContext context: Context
    ): Wallet {
        val gson = Gson()
        val storageFile = FileWCSessionStore(
            File(context.cacheDir, "session_store.json").apply { createNewFile() },
            gson
        )
        return alchemy.wallet(
            WalletConnectV1(
                storageFile,
                GsonPayloadAdapter(gson),
                GsonEncryption(gson),
                Session.PeerMeta(
                    url= "https://www.alchemy.com/",
                    name = "Tips my dev",
                    description = "Give a tip id you like it",
                    icons = listOf("https://media.istockphoto.com/photos/vinnitsa-ukraine-april-5-2019-silver-ethereum-isolated-on-white-picture-id1141076025?k=20&m=1141076025&s=612x612&w=0&h=OYBiWSFX6xYKynAOxrJ26YSz52KkThCKmNFMhtWAzvI=")
                ),
                OkHttpTransport.Builder(OkHttpClient(), gson)
            )
        )
    }
}