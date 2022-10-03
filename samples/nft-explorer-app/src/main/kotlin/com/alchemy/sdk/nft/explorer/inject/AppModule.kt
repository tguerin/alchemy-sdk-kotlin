package com.alchemy.sdk.nft.explorer.inject

import com.alchemy.sdk.core.Alchemy
import com.alchemy.sdk.core.model.AlchemySettings
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.Dispatchers
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Singleton
    @Provides
    fun provideAlchemy() = Alchemy.with(AlchemySettings())

}