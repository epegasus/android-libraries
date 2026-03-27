package com.sohaib.downloadmanager.domain.useCases

import android.util.Log
import com.sohaib.downloadmanager.utilities.ConstantUtils.TAG

/**
 * Created by: Sohaib Ahmed
 * Date: 3/5/2025
 *
 * Links:
 * - LinkedIn: https://linkedin.com/in/epegasus
 * - GitHub: https://github.com/epegasus
 */

class UseCaseUrl {

    fun validateUrl(query: String): Boolean {
        Log.d(TAG, "UseCaseUrl: validateUrl: $query")
        val url = query.trim()
        return url.isNotEmpty() && isValidUrl(url)
    }

    private fun isValidUrl(url: String): Boolean {
        val regex = "^(https?|ftp)://[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}(:\\d+)?(/.*)?$".toRegex()
        return url.matches(regex)
    }
}