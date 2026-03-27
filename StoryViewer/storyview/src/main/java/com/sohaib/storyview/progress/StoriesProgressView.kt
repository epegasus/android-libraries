package com.sohaib.storyview.progress

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.LinearLayout
import com.sohaib.storyview.R
import com.sohaib.storyview.listeners.ProgressListener
import com.sohaib.storyview.listeners.StoriesListener

/**
 * Created by Sohaib Ahmed on 02/04/2023.
 * github -> https://github.com/epegasus
 * linked-in -> https://www.linkedin.com/in/epegasus
 */

open class StoriesProgressView : LinearLayout {

    private val progressBarLayoutParams = LayoutParams(50, LayoutParams.WRAP_CONTENT, 1f)
    private val spaceLayoutParams = LayoutParams(5, LayoutParams.WRAP_CONTENT)
    private val progressBarArrayList = ArrayList<PausableProgressBar>()
    private var storiesListener: StoriesListener? = null

    /**
     * pointer of running animation
     */
    private var current = -1
    private var storiesCount = -1
    private var isComplete = false
    private var isSkipStart = false
    private var isReverseStart = false

    @JvmOverloads
    constructor(context: Context, attrs: AttributeSet? = null) : super(context, attrs) {
        init(context, attrs)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init(context, attrs)
    }

    private fun init(context: Context, attrs: AttributeSet?) {
        orientation = HORIZONTAL
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.StoriesProgressView)
        storiesCount = typedArray.getInt(R.styleable.StoriesProgressView_progressCount, 0)
        typedArray.recycle()
        bindViews()
    }

    open fun bindViews() {
        progressBarArrayList.clear()
        removeAllViews()
        for (i in 0 until storiesCount) {
            val p: PausableProgressBar = createProgressBar()
            progressBarArrayList.add(p)
            addView(p)
            if (i + 1 < storiesCount) {
                addView(createSpace())
            }
        }
    }

    private fun createProgressBar(): PausableProgressBar {
        val pausableProgressBar = PausableProgressBar(context)
        pausableProgressBar.layoutParams = progressBarLayoutParams
        return pausableProgressBar
    }

    private  fun createSpace(): View {
        val view = View(context)
        view.layoutParams = spaceLayoutParams
        return view
    }

    /**
     * Set story count and create views
     * @param storiesCount story count
     */
    fun setStoriesCount(storiesCount: Int) {
        this.storiesCount = storiesCount
        bindViews()
    }

    /**
     * Set storiesListener
     * @param storiesListener StoriesListener
     */
    fun setStoriesListener(storiesListener: StoriesListener) {
        this.storiesListener = storiesListener
    }

    /**
     * Skip current story
     */
    fun skip() {
        if (isSkipStart || isReverseStart) return
        if (isComplete) return
        if (current < 0) return
        val p: PausableProgressBar = progressBarArrayList[current]
        isSkipStart = true
        p.setMax()
    }

    /**
     * Reverse current story
     */
    fun reverse() {
        if (isSkipStart || isReverseStart) return
        if (isComplete) return
        if (current < 0) return
        val p: PausableProgressBar = progressBarArrayList[current]
        isReverseStart = true
        p.setMin()
    }

    /**
     * Set a story's duration
     * @param duration millisecond
     */
    fun setStoryDuration(duration: Long) {
        for (i in progressBarArrayList.indices) {
            progressBarArrayList[i].setDuration(duration)
            progressBarArrayList[i].setCallback(callback(i))
        }
    }

    /**
     * Set stories count and each story duration
     * @param durations milli
     */
    fun setStoriesCountWithDurations(durations: LongArray) {
        storiesCount = durations.size
        bindViews()
        for (i in progressBarArrayList.indices) {
            progressBarArrayList[i].setDuration(durations[i])
            progressBarArrayList[i].setCallback(callback(i))
        }
    }

    open fun callback(index: Int): ProgressListener {
        return object : ProgressListener {
            override fun onStartProgress() {
                current = index
            }

            override fun onFinishProgress() {
                if (isReverseStart) {
                    storiesListener?.onPrev()
                    if (0 <= current - 1) {
                        val p: PausableProgressBar = progressBarArrayList[current - 1]
                        p.setMinWithoutCallback()
                        progressBarArrayList[--current].startProgress()
                    } else {
                        progressBarArrayList[current].startProgress()
                    }
                    isReverseStart = false
                    return
                }
                val next = current + 1
                if (next <= progressBarArrayList.size - 1) {
                    storiesListener?.onNext()
                    progressBarArrayList[next].startProgress()
                } else {
                    isComplete = true
                    storiesListener?.onComplete()
                }
                isSkipStart = false
            }
        }
    }

    /**
     * Start progress animation
     */
    fun startStories() {
        progressBarArrayList[0].startProgress()
    }

    /**
     * Start progress animation from specific progress
     */
    fun startStories(from: Int) {
        for (i in 0 until from) {
            progressBarArrayList[i].setMaxWithoutCallback()
        }
        progressBarArrayList[from].startProgress()
    }

    /**
     * Need to call when Activity or Fragment destroy
     */
    fun destroy() {
        for (p in progressBarArrayList) {
            p.clear()
        }
    }

    /**
     * Pause story
     */
    fun pause() {
        if (current < 0) return
        progressBarArrayList[current].pauseProgress()
    }

    /**
     * Resume story
     */
    fun resume() {
        if (current < 0) return
        progressBarArrayList[current].resumeProgress()
    }
}