package com.sikderithub.viewsgrow.utils

import android.content.Context
import android.content.ContextWrapper
import android.graphics.drawable.Drawable
import android.net.Uri
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.annotation.DrawableRes
import androidx.lifecycle.LifecycleOwner
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.Target
import com.sikderithub.viewsgrow.Model.Banner
import com.sikderithub.viewsgrow.R

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
        val glide = Glide.with(this).load(resource)
        if (applyCircle) {
            glide.apply(RequestOptions.circleCropTransform()).into(this)
        } else {
            glide.into(this)
        }
    }

    fun ImageView.setLocalImage(uri: Uri, applyCircle: Boolean = false) {
        val glide = Glide.with(this).load(uri)
        if (applyCircle) {
            glide.apply(RequestOptions.circleCropTransform()).into(this)
        } else {
            glide.into(this)
        }
    }
    fun ImageView.setUrl(uri: String, applyCircle: Boolean = false) {
        val glide = Glide.with(this).load(uri)
        if (applyCircle) {
            glide.apply(RequestOptions.circleCropTransform()).into(this)
        } else {
            glide.into(this)
        }
    }


    fun ImageView.addBanner(banner: Banner, context: Context?= null, applyCircle: Boolean = false): Boolean {
        Log.d("ImageExtension", "Action url found")

        if(!banner.error){

            val placeholderRequest =  RequestOptions().placeholder(R.drawable.sign_up)


            val glide = Glide.with(this).setDefaultRequestOptions(placeholderRequest)
                .load(banner.imageUrl)



            if (applyCircle) {
                glide.apply(RequestOptions.circleCropTransform()).into(this)
            } else {
                glide.into(this)
            }

            if(banner.actionUrl!=null && context!=null){
                Log.d("ImageExtension", "Action url found")

                this.setOnClickListener {
                    Log.d("ImageExtension", "Action url cliked")

                    CommonMethod.openLink(banner.actionUrl!!, context)
                }
            }else{
                Log.d("ImageExtension", "Action url found")
            }

            this.visibility = View.VISIBLE

            return true


        }

        return false


    }
}