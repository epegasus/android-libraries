package com.sohaib.regret

import android.graphics.Typeface
import android.widget.TextView
import com.sohaib.regret.enums.CaseType
import com.sohaib.regret.helper.Regret
import com.sohaib.regret.interfaces.RegretListener

class RegretManager(private val regretListener: RegretListener) : RegretListener {

    private val regret = Regret(this)
    private var textSticker: TextView? = null

    // Previous Settings
    private var previousText = ""
    private var previousTypeface: Typeface? = null
    private var previousTextColor = 0

    private var isUndoing = false
    var isBold = false
    var isItalic = false
    var isUnderline = false
    var isStrikeThrough = false

    fun setView(textSticker: TextView) {
        this.textSticker = textSticker
    }

    fun getView(): TextView? {
        return textSticker
    }

    /* ------------------------------------- Text ------------------------------------- */

    fun setPreviousText(previousText: String) {
        this.previousText = previousText
    }

    fun setNewText(newText: String) {
        if (!isUndoing) {
            regret.add(CaseType.TEXT, previousText, newText)
            previousText = newText
        }
    }

    /* ---------------------------------- TypeFace ---------------------------------- */

    fun setPreviousTypeFace(previousTypeface: Typeface) {
        this.previousTypeface = previousTypeface
    }

    fun setNewTypeFace(newTypeface: Typeface) {
        previousTypeface?.let {
            if (!isUndoing) {
                regret.add(CaseType.TYPEFACE, it, newTypeface)
                previousTypeface = newTypeface
            }
        } ?: throw RuntimeException("Previous Typeface is required. Set it using 'setPreviousTypeFace()'")
    }

    /* -------------------------------- Text Color ---------------------------------- */

    fun setPreviousTextColor(previousTextColor: Int) {
        this.previousTextColor = previousTextColor
    }

    fun setNewTextColor(newTextColor: Int) {
        if (!isUndoing) {
            regret.add(CaseType.TEXT_COLOR, previousTextColor, newTextColor)
            previousTextColor = newTextColor
        }
    }

    fun undo() {
        if (regret.canUndo()) {
            isUndoing = true
            regret.undo()
            isUndoing = false
        }
    }

    fun redo() {
        if (regret.canRedo()) {
            isUndoing = true
            regret.redo()
            isUndoing = false
        }
    }

    fun canUndo() = regret.canUndo()
    fun canRedo() = regret.canRedo()

    fun clear() {
        regret.clear()
    }

    val historySize: Int
        get() = regret.size

    override fun onDo(key: CaseType, value: Any?) {
        when (key) {
            CaseType.TEXT -> {
                val newText = value.toString()
                textSticker?.text = newText
                previousText = newText
            }
            CaseType.TYPEFACE -> {
                val newTypeface = value as? Typeface
                textSticker?.setTypeface(newTypeface)
                previousTypeface = newTypeface
            }
            CaseType.TEXT_COLOR -> {
                val newColorInt = value as Int
                textSticker?.setTextColor(newColorInt)
                previousTextColor = newColorInt
            }
        }
    }

    override fun onCanDo(canUndo: Boolean, canRedo: Boolean) {
        regretListener.onCanDo(canUndo, canRedo)
    }
}