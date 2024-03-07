package com.example.data.network

import com.example.data.network.genre.GenresApiService
import com.example.data.network.movie.MoviesByGenresApiService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import java.util.concurrent.TimeUnit
import retrofit2.converter.gson.GsonConverterFactory
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
     * Provides an implementation of [GenresApiService].
     */
    @Singleton
    @Provides
    fun provideGenresApiService(retrofit: Retrofit): GenresApiService {
        return retrofit.create(GenresApiService::class.java)
    }

    /**
     * Provides an implementation of [MoviesByGenresApiService].
     */
    @Singleton
    @Provides
    fun provideMovieApiService(retrofit: Retrofit): MoviesByGenresApiService {
        return retrofit.create(MoviesByGenresApiService::class.java)
    }
}
