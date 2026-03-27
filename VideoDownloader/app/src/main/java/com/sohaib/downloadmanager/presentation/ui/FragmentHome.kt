package com.sohaib.downloadmanager.presentation.ui

import android.text.Editable
import android.text.TextWatcher
import androidx.core.view.isVisible
import com.sohaib.downloadmanager.R
import com.sohaib.downloadmanager.base.BaseFragment
import com.sohaib.downloadmanager.databinding.FragmentHomeBinding
import com.sohaib.downloadmanager.utilities.extensions.pasteClipboardData
import com.sohaib.downloadmanager.utilities.extensions.showToast

class FragmentHome : BaseFragment<FragmentHomeBinding>(FragmentHomeBinding::inflate) {

    override fun onViewCreated() {
        initObservers()

        binding.etInput.addTextChangedListener(textWatcher)
        binding.mbPasteLink.setOnClickListener { onPasteClick() }
        binding.mbStartDownload.setOnClickListener { onStartDownloadClick() }
    }

    private fun initObservers() {
        mainActivity?.viewModelDownloads?.let { viewModel ->
            viewModel.validUrlLiveData.observe(viewLifecycleOwner) { isValid ->
                binding.etInput.text?.clear()
                context.showToast(R.string.starting_download)
            }
            viewModel.inValidUrlLiveData.observe(viewLifecycleOwner) { isValid ->
                context.showToast(R.string.invalid_url)
            }
        }
    }

    private fun onPasteClick() {
        val clipBoardData = context?.pasteClipboardData()
        clipBoardData?.let { text ->
            binding.etInput.setText(text)
            binding.etInput.requestFocus()
            binding.etInput.setSelection(binding.etInput.text?.length ?: 0)
        } ?: run {
            context.showToast(R.string.nothing_to_paste)
        }
    }

    private fun onStartDownloadClick() {
        val query = binding.etInput.text.toString()
        mainActivity?.viewModelDownloads?.validateUrl(query)
    }

    private val textWatcher = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        override fun afterTextChanged(s: Editable?) {
            binding.mbStartDownload.isVisible = !s.isNullOrEmpty()
        }
    }
}