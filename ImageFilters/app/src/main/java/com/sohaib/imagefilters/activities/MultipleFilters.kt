package com.sohaib.imagefilters.activities

import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.slider.Slider
import com.sohaib.imagefilters.R
import com.sohaib.imagefilters.adapters.AdapterFilters
import com.sohaib.imagefilters.databinding.ActivityMultipleFiltersBinding
import com.sohaib.imagefilters.utils.BitmapUtils
import com.sohaib.imagefilters.utils.Extensions.showToast


class MultipleFilters : AppCompatActivity() {

    private val binding by lazy { ActivityMultipleFiltersBinding.inflate(layoutInflater) }
    private val adapter by lazy { AdapterFilters { onItemClick(it) } }

    private val _list = arrayListOf<Triple<Int, String, String>>()
    private val list: List<Triple<Int, String, String>> get() = _list.toList()

    private var imageUri :Uri? = null

    private val resultLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) {
        imageUri = it
        binding.sivSource.setImageURI(it)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        initRecyclerView()
        onItemClick(0)

        binding.sivSource.setOnClickListener { resultLauncher.launch("image/*") }
        binding.slider.addOnChangeListener(sliderChangeListener)
    }

    private fun initRecyclerView() {
        _list.apply {
            add(Triple(R.drawable.a1, "a1", "@adjust brightness -0.14 @adjust contrast 0.93 @adjust saturation 0.98 @adjust exposure -0.28"))
            add(Triple(R.drawable.a2, "a2", "@adjust brightness 0.21 @adjust contrast 0.85 @adjust saturation 1.28 @adjust exposure -0.1 @adjust shadowhighlight -15 33"))
            add(Triple(R.drawable.a3, "a3", "@adjust brightness 0.15 @adjust contrast 0.71 @adjust saturation 1.56 @adjust exposure 0.04 @adjust shadowhighlight -97 -40"))
            add(Triple(R.drawable.a4, "a4", "@adjust brightness -0.15 @adjust contrast 0.98 @adjust saturation 0.55 @adjust exposure -0.04"))

            add(Triple(R.drawable.b1, "b1", "@adjust brightness -0.29 @adjust contrast 0.79 @adjust saturation 1.52 @adjust exposure -0.22 @adjust shadowhighlight 19 -66"))
            add(Triple(R.drawable.b2, "b2", "@adjust saturation 1.72 @adjust exposure -0.14"))
            add(Triple(R.drawable.b3, "b3", "@curve R(0, 0)(149, 117)(188, 188)(255, 255)G(0, 0)(227, 223)(255, 255)B(0, 0)(12, 15)(255, 255)RGB(0, 0)(159, 170)(255, 255) @adjust brightness -0.05 @adjust contrast 0.87 @adjust saturation 0.99"))
            add(Triple(R.drawable.b4, "b4", "@adjust brightness -0.44 @adjust contrast 0.59 @adjust saturation 1.52 @adjust exposure 0.28 @adjust shadowhighlight -63 49"))

            add(Triple(R.drawable.c1, "c1", "@adjust brightness 0.26 @adjust contrast 0.99 @adjust saturation 1.28 @adjust exposure 0.14 @adjust shadowhighlight -27 -13 @adjust whitebalance 0.1 0.98"))
            add(Triple(R.drawable.c2, "c2", "@adjust brightness -0.13 @adjust contrast 0.77 @adjust saturation 1.52 @adjust exposure 0.1 @adjust shadowhighlight 13 -9 @adjust colorbalance -0.07 -0.06 -0.09"))
            add(Triple(R.drawable.c3, "c3", "@adjust brightness 0.41 @adjust contrast 0.85 @adjust saturation 2.52 @adjust exposure -0.14 @vignette 0.27 1"))
            add(Triple(R.drawable.c4, "c4", "@adjust brightness -0.25 @adjust contrast 0.64 @adjust saturation 2.2 @adjust exposure 0.28 @adjust shadowhighlight 32 83"))

            add(Triple(R.drawable.d1, "d1", "@adjust brightness -0.06 @adjust contrast 0.93 @adjust saturation 0.85 @adjust exposure 0.14 @adjust brightness -0.1 @adjust saturation 1.4 @adjust contrast 0.94"))
            add(Triple(R.drawable.d2, "d2", "@adjust brightness 0.26 @adjust contrast 0.77 @adjust saturation 1.6 @adjust hue 0.0872665 @adjust shadowhighlight -101 -17 @adjust colorbalance -0.03 0.01 0.09"))
            add(Triple(R.drawable.d3, "d3", "@curve R(0, 0)(76, 94)(102, 124)(178, 183)(255, 255)G(0, 0)(119, 127)(255, 255)B(0, 0)(129, 153)(255, 255)RGB(0, 0)(109, 133)(255, 255) @adjust exposure -0.12 @adjust saturation 0.97 @adjust contrast 1.08 @adjust brightness 0.1"))
            add(Triple(R.drawable.d4, "d4", "@curve R(0, 0)(57, 49)(117, 98)(208, 218)(255, 255)G(0, 0)(33, 31)(188, 186)(255, 255)B(0, 0)(23, 27)(183, 186)(215, 207)(255, 255)RGB(0, 0)(196, 211)(255, 255) @adjust brightness -0.09 @adjust contrast 0.89 @adjust saturation 1.36 @vignette 0.26 0.97"))

            add(Triple(R.drawable.e1, "e1", "@adjust brightness -0.18 @adjust contrast 0.79 @adjust saturation 1.68 @adjust contrast 0.67 @adjust exposure 0.04 @adjust shadowhighlight -49 59 @adjust colorbalance -0.11 0.02 0 @curve G(0, 0)(46, 46)(255, 255)RGB(0, 0)(60, 52)(255, 255)"))
            add(Triple(R.drawable.e2, "e2", "@adjust brightness 0.17 @adjust contrast 0.75 @adjust saturation 1.56 @adjust exposure -0.42 @adjust shadowhighlight 19 93 @vignette 0.33 0.59 @adjust hsl 0.01 0.18 -0.09"))
            add(Triple(R.drawable.e3, "e3", "@adjust contrast 0.64 @adjust brightness -0.13 @adjust exposure 0.28 @adjust saturation 1.2 @adjust hsl 0.01 0.01 -0.11 @adjust whitebalance -0.18 0.93 @adjust colorbalance 0.09 0 0 @adjust exposure 0.14"))
            add(Triple(R.drawable.e4, "e4", "@curve R(0, 0)(189, 173)(255, 255)G(0, 0)(159, 154)(189, 189)(255, 255)B(0, 0)(81, 94)(255, 255)RGB(0, 0)(79, 70)(119, 109)(255, 255) @adjust brightness -0.09 @adjust contrast 0.87 @adjust saturation 0.91 @adjust shadowhighlight -63 3 @adjust hsl -0.01 -0.06 -0.1 @adjust whitebalance 0.15 1.12 @adjust colorbalance 0.03 0 0"))

            add(Triple(R.drawable.f1, "f1", "@adjust brightness -0.28 @adjust contrast 1.24 @adjust saturation 0.77 @adjust exposure 0.14 @adjust shadowhighlight -37 -52 @adjust hsl 0.01 0.18 0.07 @adjust colorbalance 0.02 0 0"))
            add(Triple(R.drawable.f2, "f2", "@curve R(0, 0)(159, 132)(255, 255)G(0, 0)(62, 90)(141, 137)(189, 192)(255, 255)B(0, 0)(108, 98)(255, 255)RGB(0, 0)(79, 58)(255, 255) @adjust brightness -0.22 @adjust contrast 0.68 @adjust saturation 1.4 @adjust exposure 0.22 @adjust shadowhighlight -162 -30 @vignette 0.28 1 @adjust colorbalance 0.03 0 0"))
            add(Triple(R.drawable.f3, "f3", "@adjust brightness -0.14 @adjust contrast 0.86 @adjust saturation 0.77 @adjust exposure -0.2 @adjust shadowhighlight -99 119 @adjust whitebalance -0.11 0.98"))
            add(Triple(R.drawable.f4, "f4", "@curve R(0, 0)(46, 25)(255, 255)G(0, 0)(54, 57)(255, 255)B(0, 0)(82, 54)(255, 255)RGB(0, 0)(46, 31)(255, 255) @adjust brightness -0.07 @adjust contrast 0.93 @adjust saturation 1.08 @adjust shadowhighlight 3 53 @adjust level 0.07 0.93 0.93"))

            add(Triple(R.drawable.g1, "g1", "@curve R(0, 0)(46, 25)(255, 255)G(0, 0)(54, 57)(255, 255)B(0, 0)(82, 54)(255, 255)RGB(0, 0)(46, 31)(255, 255) @adjust brightness -0.07 @adjust contrast 0.93 @adjust saturation 1.08 @adjust shadowhighlight 3 53 @adjust level 0.07 0.93 0.93 @adjust brightness 0.22 @adjust contrast 0.71 @adjust saturation 1.12 @adjust exposure -0.06 @adjust shadowhighlight 28 -42 @adjust whitebalance 0.11 1.04 @adjust colorbalance -0.09 0 0 @adjust exposure 0.02"))
            add(Triple(R.drawable.g2, "g2", "@curve R(0, 0)(70, 94)(76, 95)(137, 157)(154, 157)(173, 157)(255, 255)G(0, 0)(84, 76)(87, 81)(232, 245)(255, 255)B(0, 0)(97, 100)(255, 255)RGB(0, 0)(143, 156)(196, 199)(255, 255)"))
            add(Triple(R.drawable.g3, "g3", "@adjust brightness 0.33 @adjust contrast 0.79 @adjust saturation 0.56 @adjust exposure 0.02 @adjust hsl -0.01 1 -0.01 @adjust colorbalance -0.18 0 0 @adjust saturation 1.36"))
            add(Triple(R.drawable.g4, "g4", "@curve R(0, 0)(78, 35)(79, 33)(151, 86)(219, 192)(255, 255)G(0, 0)(135, 141)(255, 255)B(0, 0)(124, 143)(255, 255)RGB(0, 0)(116, 122)(133, 129)(255, 255) @adjust contrast 1.08 @adjust brightness 0.11 @adjust saturation 1.4 @vignette 0.29 0.81 @adjust hsl -0.02 -0.07 0.06 @adjust colorbalance 0 0.05 0.09"))

            add(Triple(R.drawable.h1, "h1", "@adjust brightness -0.28 @adjust contrast 1.72 @adjust saturation 1.2 @curve R(0, 0)(143, 103)(255, 255)G(0, 0)(130, 109)(255, 255)B(0, 0)(129, 125)(153, 151)(255, 255)RGB(0, 0)(86, 89)(94, 97)(255, 255)"))
            add(Triple(R.drawable.h2, "h2", "@curve R(0, 0)(127, 108)(255, 255)G(0, 0)(70, 50)(255, 255)B(0, 0)(114, 98)(255, 255)RGB(0, 0)(159, 105)(255, 255) @adjust shadowhighlight 30 61"))
            add(Triple(R.drawable.h3, "h3", "@adjust brightness -0.23 @adjust contrast 1.4 @adjust saturation 0.83 @adjust exposure -0.34 @adjust shadowhighlight -15 128 @vignette 0.41 0.97 @adjust whitebalance 0.26 0.98"))
            add(Triple(R.drawable.h4, "h4", "@adjust brightness -0.26 @adjust contrast 0.52 @adjust saturation 0.98 @adjust shadowhighlight -128 198 @adjust hsl 0.02 0.65 -0.05 @adjust whitebalance -0.07 0.98"))

            add(Triple(R.drawable.i1, "i1", "@adjust brightness 0.29 @adjust contrast 1.2 @adjust saturation 1.24 @adjust exposure -0.3 @adjust shadowhighlight 44 41 @vignette 0.08 0.97 @adjust whitebalance -0.03 0.99 @adjust colorbalance 0 -0.02 -0.09"))
            add(Triple(R.drawable.i2, "i2", "@curve R(0, 0)(199, 184)(255, 255)G(0, 0)(58, 47)(173, 181)(188, 191)(255, 255)B(0, 0)(108, 149)(255, 255)RGB(0, 0)(191, 216)(255, 255) @adjust contrast 0.85 @adjust brightness -0.13 @adjust saturation 1.76 @adjust exposure 0.14 @adjust shadowhighlight 46 -44 @adjust colorbalance -0.25 0 0 @adjust whitebalance 0.05 0.98"))
            add(Triple(R.drawable.i3, "i3", "@adjust brightness -0.34 @adjust contrast 0.7 @adjust saturation 0.63 @adjust exposure 0.58 @adjust shadowhighlight -166 -13 @adjust hsl -0.01 0.56 0.09 @adjust whitebalance -0.1 0.97 @adjust colorbalance -0.07 0 0 @adjust level 0.09 0.95 0.93 @curve RGB(0, 0)(135, 146)(255, 255)"))
            add(Triple(R.drawable.i4, "i4", "@adjust exposure -0.46 @adjust saturation 2.16 @adjust contrast 0.71 @adjust brightness 0.09 @adjust shadowhighlight -71 152 @adjust whitebalance -0.11 1.04 @adjust saturation 0.79 @adjust brightness 0.19"))

            add(Triple(R.drawable.j1, "j1", "@adjust exposure -0.46 @adjust saturation 2.16 @adjust contrast 0.71 @adjust brightness 0.09 @adjust shadowhighlight -71 152 @adjust whitebalance -0.11 1.04 @adjust saturation 0.79 @adjust brightness 0.19 @adjust brightness -0.34 @adjust contrast 1.44 @adjust saturation 0.82 @adjust exposure 0.36 @adjust shadowhighlight -13 -68"))
            add(Triple(R.drawable.j2, "j2", "@curve R(0, 0)(114, 98)(255, 255)G(0, 0)(172, 170)(255, 255)B(0, 0)(122, 86)(255, 255)RGB(0, 0)(137, 172)(255, 255) @adjust brightness -0.14 @adjust contrast 0.93 @adjust saturation 0.82 @adjust exposure -0.12 @adjust shadowhighlight -101 200"))
            add(Triple(R.drawable.j3, "j3", "@adjust brightness -0.23 @adjust contrast 0.79 @adjust saturation 1.72 @adjust exposure 0.26 @adjust shadowhighlight -7 -21 @vignette 0.24 1 @adjust hsl 0.02 -0.28 -0.13"))
            add(Triple(R.drawable.j4, "j4", "@adjust brightness -0.33 @adjust contrast 0.58 @adjust saturation 2.68 @adjust exposure 0.26 @adjust shadowhighlight 15 119"))

            add(Triple(R.drawable.k1, "k1", "@adjust brightness 0.15 @adjust contrast 0.89 @adjust saturation 1.76 @adjust exposure -0.02 @adjust shadowhighlight 84 -30 @adjust colorbalance -0.13 0 0"))
            add(Triple(R.drawable.k2, "k2", "@curve R(0, 0)(199, 183)(255, 255)G(0, 0)(68, 52)(140, 135)(186, 207)(255, 255)B(0, 0)(102, 84)(255, 255)RGB(0, 0)(183, 216)(255, 255) @adjust exposure 0.02 @adjust saturation 0.87 @adjust colorbalance -0.15 0 0 @adjust hsl 0.02 0.11 -0.03 @adjust exposure -0.22"))
            add(Triple(R.drawable.k3, "k3", "@adjust brightness 0.12 @adjust brightness 0.69 @adjust contrast 0.77 @adjust saturation 3.12 @adjust exposure 0.02 @adjust shadowhighlight 100 23 @adjust whitebalance 0.03 0.95"))
            add(Triple(R.drawable.k4, "k4", "@curve R(0, 0)(111, 116)(116, 114)(255, 255)G(0, 0)(125, 135)(255, 255)B(0, 0)(129, 164)(255, 255)RGB(0, 0)(132, 151)(255, 255)"))

            add(Triple(R.drawable.l1, "l1", "@curve R(0, 0)(111, 116)(116, 114)(255, 255)G(0, 0)(125, 135)(255, 255)B(0, 0)(129, 164)(255, 255)RGB(0, 0)(132, 151)(255, 255) @adjust brightness 0.23 @adjust saturation 1.04 @adjust exposure -0.36 @adjust shadowhighlight -21 81 @adjust colorbalance 0.02 0 0"))
            add(Triple(R.drawable.l2, "l2", "@curve R(0, 0)(149, 105)(255, 255)G(0, 0)(114, 109)(255, 255)B(0, 0)(63, 63)(74, 84)(255, 255)RGB(0, 0)(172, 189)(255, 255) @adjust whitebalance 0.11 0.98 @adjust colorbalance -0.25 0 0"))
            add(Triple(R.drawable.l3, "l3", "@adjust whitebalance 0.11 0.98 @adjust colorbalance -0.25 0 0 @vignette 0.22 0.91 @adjust exposure -0.04 @adjust saturation 0.94 @adjust brightness -0.44 @adjust saturation 0.56 @adjust hsl 0.03 1 0.18"))
            add(Triple(R.drawable.l4, "l4", "@adjust brightness 0.21 @adjust contrast 0.62 @adjust saturation 0.56 @adjust shadowhighlight -158 115 @adjust hsl -0.01 0.83 -0.03 @adjust level 0.03 0.89 0.91"))

            add(Triple(R.drawable.m1, "m1", "@adjust brightness -0.25 @adjust contrast 0.66 @adjust saturation 1.4 @adjust exposure -0.36 @adjust shadowhighlight -83 186 @vignette 0.4 0.96 @adjust hsl 0.02 -0.11 0.26"))
            add(Triple(R.drawable.m2, "m2", "@adjust brightness 0.14 @adjust contrast 1.2 @adjust contrast 0.89 @adjust saturation 0.99 @adjust exposure -0.14 @vignette 0.21 0.97 @adjust whitebalance 0.06 1.04 @adjust colorbalance -0.03 -0.01 0.01 @adjust level 0.07 0.9 1.19"))
            add(Triple(R.drawable.m3, "m3", "@curve R(0, 0)(84, 55)(137, 130)(255, 255)G(0, 0)(159, 156)(255, 255)B(0, 0)(92, 74)(255, 255)RGB(0, 0)(138, 140)(255, 255) @adjust brightness 0.26 @adjust contrast 0.64 @adjust saturation 0.67 @adjust exposure -0.2 @adjust exposure 0.22 @adjust shadowhighlight -93 140 @adjust hsl -0.02 0.13 -0.05 @adjust whitebalance -0.03 1.04 @adjust colorbalance 0.13 0 0 @adjust level 0.05 1 1.03 @style edge 0.14 0.6 @style emboss 0.01 0.6 0.15 @style haze -0.11 0.005 1 1 1 @style haze 0.05 0.225 1 1 1"))
            add(Triple(R.drawable.m4, "m4", "@adjust brightness -0.34 @adjust contrast 0.67 @adjust saturation 0.98 @adjust exposure 0.3 @adjust hue 0.244346 @adjust shadowhighlight -192 35 @vignette 0.11 0.68 @adjust whitebalance -0.23 0.95 "))

            add(Triple(R.drawable.n1, "n1", "@adjust brightness -0.33 @adjust contrast 0.63 @adjust saturation 1.56 @adjust exposure 0.36 @adjust shadowhighlight -121 7 @vignette 0.19 0.53 @adjust whitebalance 0.09 0.98 @adjust colorbalance -0.1 0 0"))
            add(Triple(R.drawable.n2, "n2", "@adjust brightness 0.44 @adjust contrast 0.79 @adjust saturation 1.4 @adjust shadowhighlight -53 43 @adjust whitebalance -0.18 1.08 @adjust exposure -0.2 @adjust level 0.17 0.97 0.77"))
            add(Triple(R.drawable.n3, "n3", "@adjust brightness -0.14 @adjust contrast 0.86 @adjust saturation 0.44 @adjust exposure 0.12 @adjust hsl 0.01 1 -0.03 @adjust whitebalance -0.1 0.98 @adjust level 0.05 0.93 1.03"))
            add(Triple(R.drawable.n4, "n4", "@adjust brightness 0.26 @adjust contrast 0.68 @adjust saturation 2.36 @adjust exposure -0.2 @adjust shadowhighlight 58 15 @adjust hsl 0.02 -0.3 0.11 @adjust whitebalance -0.06 0.95 @adjust level 0.1 0.97 0.97 @vignette 0.42 0.65"))
        }
        binding.rvList.adapter = adapter
        adapter.submitList(list)
    }

    private fun onItemClick(position: Int) {
        val item = list[position]
        val bitmap : Bitmap? = imageUri?.let {
            MediaStore.Images.Media.getBitmap(contentResolver, it)
        } ?: run {
            BitmapUtils.getBitmap(this, R.drawable.img_dummy_2)
        }

        bitmap?.let {bit ->
            binding.svProcessing.setImageBitmap(bit)
            binding.svProcessing.setFilterWithConfig(item.third)
            binding.svProcessing.setFilterIntensity(1f)
        } ?: showToast("Bitmap is null")
        binding.slider.value = 1f
    }

    private val sliderChangeListener = Slider.OnChangeListener { _, value, _ ->
        binding.svProcessing.setFilterIntensity(value)
    }
}