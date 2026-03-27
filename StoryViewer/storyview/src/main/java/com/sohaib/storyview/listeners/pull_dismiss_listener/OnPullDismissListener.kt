package com.sohaib.storyview.listeners.pull_dismiss_listener

/**
 * Created by Sohaib Ahmed on 02/04/2023.
 * github -> https://github.com/epegasus
 * linked-in -> https://www.linkedin.com/in/epegasus
 */

interface OnPullDismissListener {

    /**
     * Layout is pulled down to dismiss
     * Good time to finish activity, remove fragment or any view
     */
    fun onDismissed()

    /**
     * Convenient method to avoid layout_color overriding event
     * If you have a RecyclerView or ScrollerView in our layout_color your can
     * avoid PullDismissLayout to handle event.
     *
     * @return true when ignore pull down event, f
     * false for allow PullDismissLayout handle event
     */
    fun onShouldInterceptTouchEvent(): Boolean
}