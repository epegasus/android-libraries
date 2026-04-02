package com.sohaib.dragdrop.helper

import android.content.ClipData
import android.content.ClipDescription
import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.util.Log
import android.view.DragEvent
import android.view.View
import android.view.ViewGroup
import android.widget.Toast

class DADHelper(private val context: Context, private val textMessage: String) {

    private val label by lazy { "key" }

    /**
     *  -> Old way of getting clipData
     *
     *  val clipItem = ClipData.Item(textMessage)
     *  val mimeType = arrayOf(ClipDescription.MIMETYPE_TEXT_PLAIN)
     *  val clipData = ClipData(label, mimeType, clipItem)
     *
     *  @param view: ImageView (picture, going to be dragged)
     */

    fun startDraggingView(view: View): Boolean {
        val clipData = ClipData.newPlainText(label, textMessage)
        val shadowBuilder = View.DragShadowBuilder(view)
        view.startDragAndDrop(clipData, shadowBuilder, view, 0)
        vibrate()
        return true
    }

    private fun vibrate() {
        val vibrator: Vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
            vibratorManager.defaultVibrator
        } else {
            context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            vibrator.vibrate(VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE))
        else
            vibrator.vibrate(5000)
    }

    /**
     *  invalidate():   view forced to be redraw to update UI.
     *  hasMimeType():  true: if its a source
     */

    val dragListener = View.OnDragListener { view, event ->
        val draggedView = event.localState as View

        when (event.action) {
            DragEvent.ACTION_DRAG_STARTED -> event.clipDescription.hasMimeType(ClipDescription.MIMETYPE_TEXT_PLAIN)

            DragEvent.ACTION_DRAG_ENTERED,
            DragEvent.ACTION_DRAG_EXITED,
            DragEvent.ACTION_DRAG_ENDED,
                -> {
                view.invalidate()
                true
            }

            DragEvent.ACTION_DRAG_LOCATION -> true

            DragEvent.ACTION_DROP -> {
                // Gets the item containing the dragged data.
                val clipDataItem: ClipData.Item = event.clipData.getItemAt(0)
                val srcParent = draggedView.parent as ViewGroup
                val dstParent = view as ViewGroup

                srcParent.removeView(draggedView)
                dstParent.addView(draggedView)

                Toast.makeText(context, "${clipDataItem.text}", Toast.LENGTH_LONG).show()
                view.invalidate()
                true
            }

            else -> {
                // An unknown action type was received.
                Log.e("MyTag", "Unknown action type received by View.OnDragListener.")
                false
            }
        }
    }
}