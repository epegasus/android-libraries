package com.sohaib.kleanbot.di

import android.content.Context
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import com.sohaib.kleanbot.data.dataSources.DataSourceRemoteOpenAI
import com.sohaib.kleanbot.data.dataSources.retrofit.RetrofitInstanceOpenAI
import com.sohaib.kleanbot.data.repository.RepositoryOpenAIImpl
import com.sohaib.kleanbot.domain.useCases.UseCaseOpenAI
import com.sohaib.kleanbot.presentation.viewModel.ViewModelOpenAI
import com.sohaib.kleanbot.presentation.viewModel.ViewModelProviderOpenAI
import com.sohaib.kleanbot.core.InternetManager

/**
 * Created by: Sohaib Ahmed
 * Date: 5/7/2025
 *
 * Links:
 * - LinkedIn: https://linkedin.com/in/epegasus
 * - GitHub: https://github.com/epegasus
 */

class DIManual {

    fun getViewModelOpenAI(context: Context, owner: ViewModelStoreOwner): ViewModelOpenAI {
        val apiInterface = RetrofitInstanceOpenAI.api
        val dataSource = DataSourceRemoteOpenAI(apiInterface)
        val repository = RepositoryOpenAIImpl(dataSource)
        val internetManager = InternetManager(context)
        val useCase = UseCaseOpenAI(repository, internetManager)
        val factory = ViewModelProviderOpenAI(useCase)
        return ViewModelProvider(owner, factory)[ViewModelOpenAI::class.java]
    }
}