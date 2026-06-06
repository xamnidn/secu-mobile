package com.secu.app.utils

import android.content.Context

object AppClipboardManager {
    fun copy(context: Context, text: String) = com.secu.app.data.ClipboardManager.copy(context, text)
    fun overwrite(context: Context) = com.secu.app.data.ClipboardManager.overwrite(context)
}