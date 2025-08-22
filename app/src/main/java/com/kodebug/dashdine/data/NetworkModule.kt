package com.kodebug.dashdine.data

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    // This function provides a customized OkHttpClient instance using Hilt's @Provides annotation
    @Provides
    fun provideClient(session: DashDineSession): OkHttpClient {
        // Create a new OkHttpClient builder
        val client = OkHttpClient.Builder()
        // Add an interceptor to automatically include an Authorization header with each request
        client.addInterceptor { chain ->
            val request = chain.request().newBuilder()
                // Add a Bearer token from session storage to the request headers
                .addHeader("Authorization", "Bearer ${session.getToken()}")
                .build()
            // Proceed with the request using the modified request
            chain.proceed(request)
        }
        // Add a logging interceptor for debugging purposes (optional)
        client.addInterceptor(HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        })

        // Build and return the customized OkHttpClient instance
        return client.build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(client: OkHttpClient) : Retrofit {
        return Retrofit.Builder()
            .client(client)
            .baseUrl("http://10.0.2.2:8080")
//            .baseUrl("http://192.168.156.186:8080")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }


    @Provides
    fun provideApiService(retrofit: Retrofit) : DashDineApiService {
        return retrofit.create(DashDineApiService::class.java)
    }

    @Provides
    fun provideDashDineSession(@ApplicationContext context: Context) : DashDineSession {
        return DashDineSession(context)
    }
}