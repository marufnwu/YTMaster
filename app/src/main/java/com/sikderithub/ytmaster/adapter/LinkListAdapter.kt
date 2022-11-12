package com.sikderithub.ytmaster.adapter

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat.getSystemService
import androidx.core.widget.ImageViewCompat
import androidx.recyclerview.widget.RecyclerView
import com.sikderithub.ytmaster.Model.Link
import com.sikderithub.ytmaster.R
import com.sikderithub.ytmaster.databinding.GenerateLinkItemLayoutBinding
import com.sikderithub.ytmaster.databinding.LayoutLinkThumbnailBinding
import com.sikderithub.ytmaster.ui.all_link.AllLinkActivity
import com.sikderithub.ytmaster.utils.CommonMethod
import com.sikderithub.ytmaster.utils.MyExtensions.setUrl
import com.sikderithub.ytmaster.utils.MyExtensions.shortToast

class LinkListAdapter(val context: Context, val links:MutableList<Link>, val viewType: ViewType) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    enum class ViewType{
        LINK,
        THUMBNAIL
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {


        if(viewType == ViewType.LINK.ordinal){
            return MyViewHolder(GenerateLinkItemLayoutBinding.inflate(LayoutInflater.from(context),  parent, false))
        }else{
            return ThumbViewHolder(LayoutLinkThumbnailBinding.inflate(LayoutInflater.from(context),  parent, false))
        }

    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if(viewType == ViewType.LINK){
            (holder as MyViewHolder).onBind(links[position], context)
        }else if(viewType == ViewType.THUMBNAIL){
            (holder as ThumbViewHolder).onBind(links[position], context)
        }
    }

    override fun getItemCount(): Int {
       return links.size
    }

    override fun getItemViewType(position: Int): Int {
        return viewType.ordinal
    }
}

class MyViewHolder(val binding : GenerateLinkItemLayoutBinding) : RecyclerView.ViewHolder(binding.root) {
    fun onBind(link: Link, context:Context){




        link.click?.let {
            binding.txtViews.text = it.toString()
        }

        link.nLink?.let {link->
            binding.txtNLink.text = link
            binding.imgCopy.setOnClickListener {
                val clipboardManager = context.getSystemService(AppCompatActivity.CLIPBOARD_SERVICE) as ClipboardManager
                val clipData = ClipData.newPlainText(
                    "link",
                    link
                )
                clipboardManager.setPrimaryClip(clipData)
                context.shortToast("Link Copied")
            }
        }

        link.metaTitle?.let {
            binding.txtMetaTitle.text = it
        }

        link.type?.let {
            binding.txtType.text = it
        }



    }
}

class ThumbViewHolder(val binding : LayoutLinkThumbnailBinding) : RecyclerView.ViewHolder(binding.root){
    fun onBind(link: Link, context: Context){


        if(context is AllLinkActivity){
            binding.root.layoutParams.width = LinearLayout.LayoutParams.MATCH_PARENT
            binding.imgThumb.layoutParams.height = context.resources.getDimension(com.intuit.sdp.R.dimen._160sdp).toInt()
            binding.imgThumb.layoutParams.width = binding.root.layoutParams.width
        }

        link.chLogo?.let {
            binding.imgLogo.setUrl(it)
        }

        link.metaImg?.let {
            binding.imgThumb.setUrl(it)
        }

        link.metaTitle?.let {
            binding.txtTitle.text = it
        }

        link.chName?.let {
            binding.txtChannelName.text = it
        }

        link.nLink?.let {link->
            binding.root.setOnClickListener {
                CommonMethod.openLink(link, context)
            }
        }
    }
}
