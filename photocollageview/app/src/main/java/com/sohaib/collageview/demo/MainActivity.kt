package com.sohaib.collageview.demo

import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.net.toUri
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.doOnPreDraw
import androidx.core.view.updatePadding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.sohaib.collageview.demo.adapter.CollageAdapter
import com.sohaib.collageview.demo.databinding.ActivityMainBinding
import com.sohaib.collageview.demo.model.CollageShapeItem
import com.sohaib.collageview.factory.CollageViewFactory
import com.sohaib.collageview.utils.ImageUtil

class MainActivity : AppCompatActivity() {

    private val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }
    private val adapter by lazy { CollageAdapter { shape -> binding.collageView.currentLayoutType = shape.layoutType } }
    private val imageUtil by lazy { ImageUtil() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        fullScreen()
        initRecyclerView()
        initCollageView()

        binding.toolbar.setOnMenuItemClickListener(menuItemClickListener)
    }

    private fun fullScreen() {
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val bars = insets.getInsets(WindowInsetsCompat.Type.systemBars() or WindowInsetsCompat.Type.displayCutout())
            v.updatePadding(left = bars.left, top = bars.top, right = bars.right, bottom = bars.bottom)
            WindowInsetsCompat.CONSUMED
        }
    }

    private fun initRecyclerView() {
        binding.rvShapes.adapter = adapter
        adapter.submitList(createShapeItems())
    }

    private fun initCollageView() {
        binding.collageView.setImagesFromUris(sampleUris())
        // Ensure default layout (last item) is applied after view is measured
        binding.collageView.doOnPreDraw {
            createShapeItems().lastOrNull()?.let { last ->
                binding.collageView.currentLayoutType = last.layoutType
            }
        }
    }

    private fun sampleUris(): List<Uri> = listOf(
        "android.resource://${packageName}/${R.drawable.image_1}".toUri(),
        "android.resource://${packageName}/${R.drawable.image_2}".toUri(),
        "android.resource://${packageName}/${R.drawable.image_3}".toUri(),
        "android.resource://${packageName}/${R.drawable.image_4}".toUri(),
        "android.resource://${packageName}/${R.drawable.image_5}".toUri(),
        "android.resource://${packageName}/${R.drawable.image_6}".toUri()
    )

    private fun createShapeItems(): List<CollageShapeItem> = listOf(
        CollageShapeItem(R.drawable.ic_collage_2_image_vertical, CollageViewFactory.CollageLayoutType.TWO_IMAGE_VERTICAL),
        CollageShapeItem(R.drawable.ic_collage_2_image_horizontal, CollageViewFactory.CollageLayoutType.TWO_IMAGE_HORIZONTAL),
        CollageShapeItem(R.drawable.ic_collage_type_3image0, CollageViewFactory.CollageLayoutType.THREE_IMAGE_0),
        CollageShapeItem(R.drawable.ic_collage_type_3image1, CollageViewFactory.CollageLayoutType.THREE_IMAGE_1),
        CollageShapeItem(R.drawable.ic_collage_type_3image2, CollageViewFactory.CollageLayoutType.THREE_IMAGE_2),
        CollageShapeItem(R.drawable.ic_collage_type_3image3, CollageViewFactory.CollageLayoutType.THREE_IMAGE_3),
        CollageShapeItem(R.drawable.ic_collage_type_3image5, CollageViewFactory.CollageLayoutType.THREE_IMAGE_VERTICAL),
        CollageShapeItem(R.drawable.ic_collage_type_3image4, CollageViewFactory.CollageLayoutType.THREE_IMAGE_HORIZONTAL),
        CollageShapeItem(R.drawable.ic_collage_type_4image0, CollageViewFactory.CollageLayoutType.FOUR_IMAGE_0),
        CollageShapeItem(R.drawable.ic_collage_type_4image1, CollageViewFactory.CollageLayoutType.FOUR_IMAGE_1),
        CollageShapeItem(R.drawable.ic_collage_type_4image2, CollageViewFactory.CollageLayoutType.FOUR_IMAGE_2),
        CollageShapeItem(R.drawable.ic_collage_type_4image3, CollageViewFactory.CollageLayoutType.FOUR_IMAGE_3),
        CollageShapeItem(R.drawable.ic_collage_type_4image4, CollageViewFactory.CollageLayoutType.FOUR_IMAGE_4),
        CollageShapeItem(R.drawable.ic_collage_type_6image1, CollageViewFactory.CollageLayoutType.SIX_IMAGE_1),
    )

    private val menuItemClickListener = Toolbar.OnMenuItemClickListener {
        if (it.itemId == R.id.action_save) {
            saveToGallery()
            return@OnMenuItemClickListener true
        }
        return@OnMenuItemClickListener false
    }

    private fun saveToGallery() {
        MaterialAlertDialogBuilder(this)
            .setTitle(R.string.save_dialog_title)
            .setMessage(R.string.save_dialog_message)
            .setNegativeButton(android.R.string.cancel, null)
            .setPositiveButton(R.string.save_dialog_confirm) { _, _ ->
                imageUtil.saveViewToGallery(this, binding.collageView, object : ImageUtil.ImageSavedListener {
                    override fun onCollageSavedToGallery(isSaveSuccessful: Boolean, uri: Uri?) {
                        showToast(isSaveSuccessful)
                    }

                    override fun onReadyToShareImage(uri: Uri?) = Unit
                })
            }
            .show()
    }

    private fun showToast(isSaveSuccessful: Boolean) {
        val text = if (isSaveSuccessful) "Saved to gallery" else "Save failed"
        Toast.makeText(this@MainActivity, text, Toast.LENGTH_LONG).show()
    }
}