package com.example.data.network

import com.example.data.network.movie.IFilmDetailsApiService
import com.example.data.network.movie.IFilmLinksApiService
import com.example.data.network.movie.IFilmsListByGenresApiService
import com.example.data.network.genre.IGenresApiService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

/**
 * Dagger module for providing network dependencies.
 */
@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    private const val BASE_URL = "https://kinopoiskapiunofficial.tech/"

    private val interceptor: Interceptor = Interceptor { chain ->
        val request = chain.request().newBuilder()
            .addHeader("Content-Type", "application/json")
            .addHeader("accept", "application/json")
            .addHeader("X-API-KEY", "22aac2f5-6099-4f82-ac29-ebd00271936c").build()
        chain.proceed(request)
    }

    private val okHttpClient: OkHttpClient = OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS)
        .addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .addInterceptor(interceptor)
        .build()

    /**
     * Provides a Retrofit instance.
     */
    @Singleton
    @Provides
    fun provideRetrofit(): Retrofit {
        return Retrofit.Builder()
            .client(okHttpClient)
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    /**
     * Provides an implementation of [IGenresApiService].
     */
    @Singleton
    @Provides
    fun provideGenresApiService(retrofit: Retrofit): IGenresApiService {
        return retrofit.create(IGenresApiService::class.java)
    }

    /**
     * Provides an implementation of [IFilmsListByGenresApiService].
     */
    @Singleton
    @Provides
    fun provideMovieApiService(retrofit: Retrofit): IFilmsListByGenresApiService {
        return retrofit.create(IFilmsListByGenresApiService::class.java)
    }

    @Singleton
    @Provides
    fun provideMovieDetailsApiService(retrofit: Retrofit): IFilmDetailsApiService {
        return retrofit.create(IFilmDetailsApiService::class.java)
    }

    @Singleton
    @Provides
    fun provideMovieLinksApiService(retrofit: Retrofit): IFilmLinksApiService {
        return retrofit.create(IFilmLinksApiService::class.java)
    }

}
