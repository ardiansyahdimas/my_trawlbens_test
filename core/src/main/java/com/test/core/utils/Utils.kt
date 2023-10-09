package com.test.core.utils

import android.app.Activity
import android.widget.ProgressBar
import androidx.core.view.isVisible
import androidx.room.TypeConverter
import com.test.core.R
import org.aviran.cookiebar2.CookieBar

object Utils {
    fun updateUI(isLoading:Boolean, progressBar: ProgressBar){
        progressBar.isVisible = isLoading
    }

    @TypeConverter
    fun toList(value: String?): List<Int>? {
        val numberStrings = value
            ?.replace("[", "")
            ?.replace("]", "")
            ?.split(",")
        return numberStrings?.map { it.trim().toInt() }
    }

    fun showNotif(activity: Activity, title:String, message: String){
        CookieBar.build(activity)
            .setTitle(title)
            .setBackgroundColor(R.color.primary_color)
            .setMessage(message)
            .show()
    }
}