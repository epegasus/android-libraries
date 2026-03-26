package com.sohaib.regret.demo

import android.graphics.Typeface
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import com.sohaib.regret.RegretManager
import com.sohaib.regret.demo.databinding.ActivityMainBinding
import com.sohaib.regret.demo.helper.DemoHelper
import com.sohaib.regret.demo.utils.animatePulse
import com.sohaib.regret.enums.CaseType
import com.sohaib.regret.interfaces.RegretListener

class MainActivity : AppCompatActivity() {

    private val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }
    private val demoHelper by lazy { DemoHelper(this) }

    private var regretManager: RegretManager? = null

    private var canUndo: Boolean = false
    private var canRedo: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setPadding()

        initRegretManager()
        updateUI()

        binding.mbUndo.setOnClickListener { performUndoRedo(isUndo = true) }
        binding.mbRedo.setOnClickListener { performUndoRedo(isUndo = false) }
        binding.mbClear.setOnClickListener { clearHistory() }
        binding.mbChangeColor.setOnClickListener { cycleTextColor() }
        binding.mbChangeTypeface.setOnClickListener { cycleTypeface() }
        binding.mbCommit.setOnClickListener { commitText() }
    }

    private fun setPadding() {
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val bars = insets.getInsets(WindowInsetsCompat.Type.systemBars() or WindowInsetsCompat.Type.displayCutout())
            v.updatePadding(left = bars.left, top = bars.top, right = bars.right, bottom = bars.bottom)
            WindowInsetsCompat.CONSUMED
        }
    }

    private fun initRegretManager() {
        val editText = binding.etTarget

        val initialText = editText.text?.toString().orEmpty().trim()
        val initialTypeface = editText.typeface ?: Typeface.DEFAULT
        val initialColor = editText.currentTextColor

        regretManager = RegretManager(regretListener).apply {
            setView(editText)
            setPreviousText(initialText)
            setPreviousTypeFace(initialTypeface)
            setPreviousTextColor(initialColor)
        }
    }

    private fun updateUI() {
        val rm = regretManager ?: run {
            val text = "History: 0 actions"
            binding.tvHistoryInfo.text = text
            binding.mbUndo.isEnabled = canUndo
            binding.mbRedo.isEnabled = canRedo
            binding.mbClear.isEnabled = false
            return
        }

        val text = "History: ${rm.historySize} actions"
        binding.tvHistoryInfo.text = text

        binding.mbUndo.isEnabled = canUndo
        binding.mbRedo.isEnabled = canRedo
        binding.mbClear.isEnabled = rm.historySize > 0
    }

    private fun performUndoRedo(isUndo: Boolean) {
        binding.cardTarget.animatePulse()

        if (isUndo) regretManager?.undo() else regretManager?.redo()

        updateUI()
    }

    private fun clearHistory() {
        binding.cardTarget.animatePulse()
        regretManager?.clear()
        updateUI()
    }

    private fun cycleTextColor() {
        binding.cardTarget.animatePulse()

        val colorInt = demoHelper.cycleTextColor(binding.etTarget)
        binding.etTarget.setTextColor(colorInt)
        regretManager?.setNewTextColor(colorInt)
    }

    private fun cycleTypeface() {
        binding.cardTarget.animatePulse()

        val typeface = demoHelper.cycleTypeface(binding.etTarget)
        binding.etTarget.typeface = typeface
        regretManager?.setNewTypeFace(typeface)
    }

    private fun commitText() {
        val newText = binding.etTarget.text?.toString().orEmpty().trim()

        if (newText.isBlank()) {
            Toast.makeText(this, "No text Found", Toast.LENGTH_SHORT).show()
            return
        }

        binding.cardTarget.animatePulse()
        regretManager?.setNewText(newText)
        updateUI()
    }

    private val regretListener = object : RegretListener {
        override fun onDo(key: CaseType, value: Any?) {
            // RegretManager updates the EditText directly via onDo().
        }

        override fun onCanDo(canUndo: Boolean, canRedo: Boolean) {
            this@MainActivity.canUndo = canUndo
            this@MainActivity.canRedo = canRedo
            updateUI()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        regretManager = null
    }
}