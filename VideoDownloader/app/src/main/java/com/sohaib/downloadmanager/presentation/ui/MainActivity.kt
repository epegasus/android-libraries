package com.sohaib.downloadmanager.presentation.ui

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.onNavDestinationSelected
import androidx.navigation.ui.setupWithNavController
import dagger.hilt.android.AndroidEntryPoint
import com.sohaib.downloadmanager.R
import com.sohaib.downloadmanager.data.dataSources.local.AppDatabase
import com.sohaib.downloadmanager.data.dataSources.local.DataSourceLocalDownloads
import com.sohaib.downloadmanager.data.dataSources.remote.DataSourceRemoteDownloads
import com.sohaib.downloadmanager.data.repository.RepositoryDownloadsImpl
import com.sohaib.downloadmanager.databinding.ActivityMainBinding
import com.sohaib.downloadmanager.domain.useCases.UseCaseDownloads
import com.sohaib.downloadmanager.domain.useCases.UseCaseUrl
import com.sohaib.downloadmanager.presentation.viewModels.ViewModelDownloads
import com.sohaib.downloadmanager.presentation.viewModels.ViewModelProviderDownloads

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }

    // MVVM
    private val dao by lazy { AppDatabase.Companion.getDatabase(this).downloadsDao() }
    private val dataSourceLocalDownloads by lazy { DataSourceLocalDownloads(dao) }
    private val dataSourceRemoteDownloads by lazy { DataSourceRemoteDownloads(this) }
    private val repositoryDownloadsImpl by lazy { RepositoryDownloadsImpl(dataSourceLocalDownloads, dataSourceRemoteDownloads) }
    private val useCaseDownloads by lazy { UseCaseDownloads(repositoryDownloadsImpl) }
    private val useCaseUrl by lazy { UseCaseUrl() }
    val viewModelDownloads by viewModels<ViewModelDownloads> { ViewModelProviderDownloads(useCaseUrl, useCaseDownloads) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        fullScreen()
        initNavController()
    }

    private fun fullScreen() {
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun initNavController() {
        val navController = (supportFragmentManager.findFragmentById(binding.fcvContainer.id) as NavHostFragment).navController

        binding.bnvBar.setupWithNavController(navController)
        binding.bnvBar.setOnItemSelectedListener { it.onNavDestinationSelected(navController) }
    }
}