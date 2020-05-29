package com.admanager.sample

import android.app.Activity
import android.content.Context
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.net.Uri
import android.util.Log
import android.view.View
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.ColorRes
import androidx.annotation.StringRes
import androidx.core.content.ContextCompat
import com.google.android.material.snackbar.Snackbar
import java.io.ByteArrayOutputStream
import java.io.IOException


fun TextView.setTextColorResId(@ColorRes int: Int) {
    setTextColor(ContextCompat.getColor(context, int))
}


fun Activity.snack(@StringRes resId: Int) {
    //Snackbar(view)
    val snackbar = Snackbar.make(
        window.decorView, resId,
        Snackbar.LENGTH_SHORT
    )
    snackbar.show()
}

fun Activity.snack(msg: String) {
    //Snackbar(view)
    val snackbar = Snackbar.make(
        window.decorView, msg,
        Snackbar.LENGTH_SHORT
    )
    snackbar.show()
}

fun Context.toast(@StringRes resId: Int) {
    Toast.makeText(this, resId, Toast.LENGTH_SHORT).show()
}

fun Context.toast(msg: String?) {
    Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
}

fun Context.toastLong(msg: String?) {
    Toast.makeText(this, msg, Toast.LENGTH_LONG).show()
}

fun View.toggleVisibility() {
    visibility = if (visibility == View.VISIBLE) View.GONE else View.VISIBLE
}

fun View.toggleInvisibility() {
    visibility = if (visibility == View.VISIBLE) View.INVISIBLE else View.VISIBLE
}

fun View.hide() {
    visibility = View.INVISIBLE
}

fun View.showOrGone(show: Boolean?) {
    visibility = if (show == true) View.VISIBLE else View.GONE
}

fun View.showOrHide(show: Boolean?) {
    visibility = if (show == true) View.VISIBLE else View.INVISIBLE
}

fun View.show() {
    visibility = View.VISIBLE
}

fun View.gone() {
    visibility = View.GONE
}

fun Bitmap.getByte(): ByteArray? {
    try {
        val bitmap = this
        logd(
            "getByte: " + bitmap.byteCount / (1024) + "kb  " + bitmap.width + "*" + bitmap.height
        )
        val baos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos)
        val bytes = baos.toByteArray()
        logd("getByte: compressed: " + bytes.size / (1024) + "kb  ")
        return bytes

    } catch (e: Exception) {
        e.printStackTrace()
        return null
    }

}

fun Drawable.drawableToBitmap(): Bitmap? {
    var bitmap: Bitmap? = null
    val drawable = this

    if (drawable is BitmapDrawable) {
        if (drawable.bitmap != null) {
            return drawable.bitmap
        }
    }

    if (drawable.intrinsicWidth <= 0 || drawable.intrinsicHeight <= 0) {
        bitmap = Bitmap.createBitmap(
            1,
            1,
            Bitmap.Config.ARGB_8888
        ) // Single color bitmap will be created of 1x1 pixel
    } else {
        bitmap = Bitmap.createBitmap(
            drawable.intrinsicWidth,
            drawable.intrinsicHeight,
            Bitmap.Config.ARGB_8888
        )
    }

    val canvas = Canvas(bitmap)
    drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight())
    drawable.draw(canvas)
    return bitmap
}

fun ImageView.getByte(): ByteArray? {
    return when (drawable) {
        null -> null
        else -> drawable.drawableToBitmap()?.getByte()
    }
}

val Int.dPtoPx: Float
    get() = (this * Resources.getSystem().displayMetrics.density)

val Int.pXtoDp: Float
    get() = (this / Resources.getSystem().displayMetrics.density)

fun Any.logd(message: String) {
    if (BuildConfig.DEBUG) Log.d(this::class.java.simpleName, message)
}

fun Any.printStackTrace(e: Throwable) {
    if (BuildConfig.DEBUG) e.printStackTrace()
}

fun View.preventDoubleClick() {
    isEnabled = false
    postDelayed({ isEnabled = true }, 600)
}

@Throws(IOException::class)
fun Uri.readBytes(context: Context): ByteArray? =
    context.contentResolver.openInputStream(this)?.readBytes()

fun TextView.underline() {
    paintFlags = paintFlags or Paint.UNDERLINE_TEXT_FLAG
}

fun Any?.toMyDouble(): Double {
    val value = this
    return if (value != null) {
        if (value is Double) {
            value as Double
        } else {
            try {
                value.toString().toDouble()
            } catch (e: java.lang.Exception) {
                0.0
            }
        }
    } else {
        0.0
    }
}

fun View.blink() {
    toggleVisibilityDelay()
}

private fun View.toggleVisibilityDelay() {
    postDelayed({
        toggleInvisibility()
        toggleVisibilityDelay()
    }, 700)
}

fun View.makeMeBlink(dration: Long = 500, offset: Long = 0) {
    val anim = AlphaAnimation(0.0f, 1.0f).apply {
        duration = dration;
        startOffset = offset;
        repeatMode = Animation.REVERSE;
        repeatCount = Animation.INFINITE;
    };
    startAnimation(anim);
}