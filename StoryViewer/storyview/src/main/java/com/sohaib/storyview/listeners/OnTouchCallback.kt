package com.sohaib.storyview.listeners

/**
 * Created by Sohaib Ahmed on 02/04/2023.
 * github -> https://github.com/epegasus
 * linked-in -> https://www.linkedin.com/in/epegasus
 */

interface OnTouchCallback {
    fun touchUp()
    fun touchPull()
    fun touchDown(xValue: Float, yValue: Float)
}