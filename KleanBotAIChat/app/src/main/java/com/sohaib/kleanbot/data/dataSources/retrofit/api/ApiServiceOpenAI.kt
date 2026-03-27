package com.sohaib.kleanbot.data.dataSources.retrofit.api

import com.sohaib.kleanbot.data.entities.OpenAIRequest
import com.sohaib.kleanbot.data.entities.OpenAIResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

/**
 * Created by: Sohaib Ahmed
 * Date: 5/7/2025
 *
 * Links:
 * - LinkedIn: https://linkedin.com/in/epegasus
 * - GitHub: https://github.com/epegasus
 */

interface ApiServiceOpenAI {
    @POST("chat/completions")
    suspend fun getChatCompletion(@Body openAIRequest: OpenAIRequest): Response<OpenAIResponse>
}