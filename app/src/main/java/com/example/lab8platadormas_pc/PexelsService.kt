// ----------------------------------------------------------------------------
// Pedro Caso
// 241286
// Lab 8 plataformas
// ----------------------------------------------------------------------------

package com.example.lab8platadormas_pc

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

private const val BASE_URL = "https://api.pexels.com/"

object PexelsService {

    private val auth = Interceptor { chain ->
        val req = chain.request().newBuilder()
            .addHeader("Authorization", BuildConfig.PEXELS_API_KEY)
            .build()
        chain.proceed(req)
    }

    private val logger = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BASIC
    }

    private val client = OkHttpClient.Builder()
        .addInterceptor(auth)
        .addInterceptor(logger)
        .build()

    private val moshi: Moshi = Moshi.Builder()
        .addLast(KotlinJsonAdapterFactory())
        .build()

    val api: PexelsApi = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(MoshiConverterFactory.create(moshi))
        .client(client)
        .build()
        .create(PexelsApi::class.java)
}