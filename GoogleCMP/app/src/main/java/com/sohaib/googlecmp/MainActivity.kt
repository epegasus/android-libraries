package com.sohaib.googlecmp

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.sohaib.googlecmp.ads.BannerAdsConfig
import com.sohaib.googlecmp.databinding.ActivityMainBinding
import com.sohaib.googlecmp.interfaces.ConsentCallback
import com.sohaib.googlecmp.controller.ConsentController

class MainActivity : AppCompatActivity() {

    private val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }
    private val consentController by lazy { ConsentController(this) }
    private val bannerAdsConfig by lazy { BannerAdsConfig(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        showConsent()

        binding.mbPrivacy.setOnClickListener {
            consentController.launchPrivacyForm {
                binding.mtvStatus.text = getString(R.string.status, it.toString())
            }
        }
    }

    private fun showConsent() {
        binding.mtvStatus.text = getString(R.string.status, "Gathering Consent Information...")
        consentController.apply {
            initConsent("A69AF72EA9855046AD0439E4A6287ADF", object : ConsentCallback {
                override fun onConsentFormLoaded() {
                    binding.mtvStatus.text = getString(R.string.status, "Consent is loaded, about to show consent form")
                    showConsentForm()
                }

                override fun onConsentFormDismissed() {
                    binding.mtvStatus.text = getString(R.string.status, "Consent Form has been dismissed")
                }

                override fun onAdsLoad(canRequestAd: Boolean) {
                    binding.mtvStatus.text = getString(R.string.status, "Ad is loading now")
                    loadAds()
                }

                override fun onPolicyStatus(required: Boolean) {
                    Log.d("TAG", "onPolicyRequired: Is-Required: $required")
                    binding.mbPrivacy.isEnabled = required
                }
            })
        }
    }

    private fun loadAds() {
        // Load ad if permitted
        val canLoadAd = consentController.canRequestAds
        Log.d("TAG", "loadAds: $canLoadAd")
        if (!canLoadAd) {
            binding.mtvStatus.text = getString(R.string.status, "Cannot load Ads")
            binding.frameLayout.removeAllViews()
            return
        }
        binding.progressBar.visibility = View.VISIBLE
        bannerAdsConfig.loadBannerAds(binding.frameLayout)
    }
}