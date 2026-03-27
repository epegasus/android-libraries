package com.sohaib.downloadmanager.presentation.ui

import android.util.Log
import com.sohaib.downloadmanager.base.BaseFragment
import com.sohaib.downloadmanager.data.entities.DownloadEntity
import com.sohaib.downloadmanager.databinding.FragmentDownloadsBinding
import com.sohaib.downloadmanager.presentation.adapters.AdapterDownloads
import com.sohaib.downloadmanager.utilities.ConstantUtils.TAG

class FragmentDownloads : BaseFragment<FragmentDownloadsBinding>(FragmentDownloadsBinding::inflate) {

    override fun onViewCreated() {
        initObservers()
    }

    private fun initObservers() {
        mainActivity?.viewModelDownloads?.fetchDownloads()

        mainActivity?.viewModelDownloads?.downloads?.observe(viewLifecycleOwner) { list ->
            initRecyclerView(list)
            list.forEach {
                Log.d(TAG, "initObservers: DownloadEntity: $it")
            }
        }
    }

    private fun initRecyclerView(list: List<DownloadEntity>) {
        val adapter = AdapterDownloads(list)
        binding.rcvList.adapter = adapter
    }
}