package com.sikderithub.ytmaster.utils

import android.content.Context
import android.content.ContextWrapper
import android.net.Uri
import android.widget.ImageView
import android.widget.Toast
import androidx.annotation.DrawableRes
import androidx.lifecycle.LifecycleOwner
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions

object MyExtensions {

    fun Context.shortToast(message: String) {
        Toast.makeText(this,message,Toast.LENGTH_SHORT).show()
    }

    fun Context.longToast(message: String) {
        Toast.makeText(this,message,Toast.LENGTH_LONG).show()
    }

    fun Context.lifecycleOwner(): LifecycleOwner? {
        var curContext = this
        var maxDepth = 20
        while (maxDepth-- > 0 && curContext !is LifecycleOwner) {
            curContext = (curContext as ContextWrapper).baseContext
        }
        return if (curContext is LifecycleOwner) {
            curContext as LifecycleOwner
        } else {
            null
        }
    }

    fun ImageView.setDrawableImage(@DrawableRes resource: Int, applyCircle: Boolean = false) {
        val glide = Glide.with(this).load(resource).diskCacheStrategy(DiskCacheStrategy.NONE)
        if (applyCircle) {
            glide.apply(RequestOptions.circleCropTransform()).into(this)
        } else {
            glide.into(this)
        }
    }

    fun ImageView.setLocalImage(uri: Uri, applyCircle: Boolean = false) {
        val glide = Glide.with(this).load(uri).diskCacheStrategy(DiskCacheStrategy.NONE)
        if (applyCircle) {
            glide.apply(RequestOptions.circleCropTransform()).into(this)
        } else {
            glide.into(this)
        }
    }
    fun ImageView.setUrl(uri: String, applyCircle: Boolean = false) {
        val glide = Glide.with(this).load(uri).diskCacheStrategy(DiskCacheStrategy.NONE)
        if (applyCircle) {
            glide.apply(RequestOptions.circleCropTransform()).into(this)
        } else {
            glide.into(this)
        }
    }
}