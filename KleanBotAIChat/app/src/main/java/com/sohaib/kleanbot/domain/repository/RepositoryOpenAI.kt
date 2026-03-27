package com.sohaib.kleanbot.domain.repository

import com.sohaib.kleanbot.data.entities.OpenAIRequest
import com.sohaib.kleanbot.data.entities.OpenAIResponse
import com.sohaib.kleanbot.presentation.uiStates.ApiResponse
import kotlinx.coroutines.flow.Flow

/**
 * Created by: Sohaib Ahmed
 * Date: 5/7/2025
 *
 * Links:
 * - LinkedIn: https://linkedin.com/in/epegasus
 * - GitHub: https://github.com/epegasus
 */

interface RepositoryOpenAI {
    suspend fun getChatCompletion(openAIRequest: OpenAIRequest): Flow<ApiResponse<OpenAIResponse>>
}