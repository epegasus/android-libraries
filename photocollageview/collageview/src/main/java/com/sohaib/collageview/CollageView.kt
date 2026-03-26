package com.sohaib.collageview

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.net.Uri
import android.util.AttributeSet
import android.widget.FrameLayout
import androidx.annotation.DrawableRes
import com.sohaib.collageview.factory.CollageViewFactory
import com.sohaib.collageview.factory.ImageSource
import com.sohaib.collageview.views.AbstractCollageView

/**
 * High level collage view users interact with.
 *
 * - Respects layout width/height (measured from XML).
 * - Internally uses [CollageViewFactory] to create the concrete collage layout.
 * - Exposes simple public APIs to set / get images in multiple formats.
 */
class CollageView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    private var collageViewFactory: CollageViewFactory? = null
    private var collageView: AbstractCollageView? = null

    /** Backing store of images (typed) user provided. */
    private var sources: MutableList<ImageSource?> = mutableListOf()

    /** Currently selected collage layout. */
    var currentLayoutType: CollageViewFactory.CollageLayoutType = CollageViewFactory.CollageLayoutType.TWO_IMAGE_VERTICAL
        set(value) {
            field = value
            rebuildCollage()
            applySourcesToCollage()
        }

    /**
     * List of layouts supported by this view.
     * UI layer (e.g. RecyclerView) can use this to show shape options.
     */
    val supportedLayouts: List<CollageViewFactory.CollageLayoutType> by lazy {
        CollageViewFactory.CollageLayoutType.entries
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        if (w > 0 && h > 0) {
            ensureFactoryInitialized(w, h)
        }
    }

    private fun ensureFactoryInitialized(width: Int, height: Int) {
        // Always rebuild factory when size changes (ensures proper measured width/height support).
        collageViewFactory = CollageViewFactory(
            context = context,
            attrs = null,
            layoutWidth = width,
            layoutHeight = height,
            isBorderEnabled = false,
            imageUris = null
        )
        rebuildCollage()
        applySourcesToCollage()
    }

    private fun rebuildCollage() {
        val factory = collageViewFactory ?: return
        collageView = factory.getView(currentLayoutType).also { view ->
            removeAllViews()
            addView(view)
        }
    }

    // region public image APIs (production-ready)

    /** Clears all images. */
    fun clearImages() {
        sources.clear()
        collageView?.reset()
    }

    fun setImageSources(newSources: List<ImageSource?>) {
        sources = newSources.toMutableList()
        applySourcesToCollage()
    }

    /** Sets images from a list of resource IDs. */
    fun setImagesFromResources(@DrawableRes resIds: List<Int>) {
        setImageSources(resIds.map { ImageSource.ResourceSource(it) })
    }

    fun setImagesFromUris(uris: List<Uri?>) {
        setImageSources(uris.map { ImageSource.UriSource(it) })
    }

    fun setImagesFromDrawables(drawables: List<Drawable?>) {
        setImageSources(drawables.map { ImageSource.DrawableSource(it) })
    }

    fun setImagesFromBitmaps(bitmaps: List<Bitmap?>) {
        setImageSources(bitmaps.map { ImageSource.BitmapSource(it) })
    }

    /** Returns current images as list of [ImageSource]. */
    fun getImageSources(): List<ImageSource?> = sources.toList()

    fun getImageUris(): List<Uri?> =
        sources.map { (it as? ImageSource.UriSource)?.uri }

    fun getResourceIds(): List<Int?> =
        sources.map { (it as? ImageSource.ResourceSource)?.resId }

    fun getBitmaps(): List<Bitmap?> =
        sources.map { (it as? ImageSource.BitmapSource)?.bitmap }

    fun getDrawables(): List<Drawable?> =
        sources.map { (it as? ImageSource.DrawableSource)?.drawable }

    /** Returns number of image slots in current collage. */
    fun getImageSlotCount(): Int = collageView?.imageCount() ?: sources.size

    // endregion

    private fun applySourcesToCollage() {
        val collage = collageView ?: return
        sources.forEachIndexed { index, src ->
            if (index >= collage.imageCount()) return@forEachIndexed
            when (src) {
                null -> collage.setImageAt(index, null as Uri?)
                is ImageSource.UriSource -> collage.setImageAt(index, src.uri)
                is ImageSource.ResourceSource -> collage.setImageAt(index, src.resId)
                is ImageSource.DrawableSource -> collage.setImageAt(index, src.drawable)
                is ImageSource.BitmapSource -> collage.setImageAt(index, src.bitmap)
            }
        }
    }

    /**
     * Exposes the underlying collage layout view for features like saving to gallery.
     */
    fun getInnerCollageView(): AbstractCollageView? = collageView
}