package com.angela.notemoment

import android.util.Log

object Logger {

    private const val TAG = "Angela-Note"

    fun v(content: String) { Log.v(TAG, content) }
    fun d(content: String) { Log.d(TAG, content) }
    fun i(content: String) { Log.i(TAG, content) }
    fun w(content: String) { Log.w(TAG, content) }
    fun e(content: String) { Log.e(TAG, content) }

}