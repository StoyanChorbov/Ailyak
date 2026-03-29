package aubg.hack.ailyak.utils

import aubg.hack.ailyak.common.Constants
import aubg.hack.ailyak.service.*

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient =
        OkHttpClient.Builder()
            .addInterceptor(HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            })
            .build()

    @Provides @Singleton @Named("iNaturalist")
    fun provideINaturalistRetrofit(client: OkHttpClient): Retrofit =
        Retrofit.Builder()
            .baseUrl(Constants.INATURALIST_BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

    @Provides @Singleton @Named("gbif")
    fun provideGbifRetrofit(client: OkHttpClient): Retrofit =
        Retrofit.Builder()
            .baseUrl(Constants.GBIF_BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

    @Provides @Singleton @Named("overpass")
    fun provideOverpassRetrofit(client: OkHttpClient): Retrofit =
        Retrofit.Builder()
            .baseUrl(Constants.OVERPASS_BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

    @Provides @Singleton
    fun providePlantApiService(@Named("iNaturalist") retrofit: Retrofit): PlantApiService =
        retrofit.create(PlantApiService::class.java)

    @Provides @Singleton
    fun provideAnimalApiService(@Named("gbif") retrofit: Retrofit): AnimalApiService =
        retrofit.create(AnimalApiService::class.java)

    @Provides @Singleton
    fun provideWaterApiService(@Named("overpass") retrofit: Retrofit): WaterSourceService =
        retrofit.create(WaterSourceService::class.java)

    @Provides @Singleton @Named("opencellid")
    fun provideOpenCellIdRetrofit(client: OkHttpClient): Retrofit =
        Retrofit.Builder()
            .baseUrl("https://opencellid.org/")
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

    @Provides @Singleton
    fun provideCellTowerApiService(
        @Named("opencellid") retrofit: Retrofit
    ): CoverageCellService = retrofit.create(CoverageCellService::class.java)
}