package com.services.provider.data.utils

import android.content.Intent
import android.os.Build
import android.os.Parcelable
import android.view.View
import com.services.provider.domain.model.Recipient
import com.services.provider.domain.model.User
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

inline fun <reified T : Parcelable> Intent.parcelable(key: String): T? = when {
    Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU -> getParcelableExtra(
        key,
        T::class.java
    )

    else -> @Suppress("DEPRECATION") getParcelableExtra(key) as? T
}


fun View.makeGone() {
    this.visibility = View.GONE
}

fun View.makeInvisible() {
    this.visibility = View.INVISIBLE
}

fun makeInvisible(vararg views: View) {
    for (view in views) {
        view.makeInvisible()
    }
}

fun View.makeVisible() {
    this.visibility = View.VISIBLE
}

fun Long.getTimeOrDate(): String {
    val today = System.currentTimeMillis()
    val yesterday = today - 86400000
    val date = Date(this)
    val format = SimpleDateFormat("dd MMM, yyyy", Locale.getDefault())
    val timeFormat = SimpleDateFormat("hh:mm aa", Locale.getDefault())
    return when {
        format.format(date) == format.format(Date(today)) -> {
            timeFormat.format(date)
        }

        format.format(date) == format.format(Date(yesterday)) -> {
            "Yesterday, ${timeFormat.format(date)}"
        }

        else -> {
            "${format.format(date)} ${timeFormat.format(date)}"
        }
    }
}

fun User.toRecepient(): Recipient {
    return Recipient(
        userId = this.id,
        userName = this.getName(),
        profilePicture = this.profilePhoto,
        lastMessageTime = 0
    )
}