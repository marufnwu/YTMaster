package com.sikderithub.ytmaster.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.sikderithub.ytmaster.Model.HighLightedChannel
import com.sikderithub.ytmaster.databinding.ChannelItemLayoutBinding
import com.sikderithub.ytmaster.databinding.RoundChannelItemLayoutBinding
import com.sikderithub.ytmaster.utils.CommonMethod
import com.sikderithub.ytmaster.utils.MyExtensions.setUrl


class ChannelsAdapter(val context: Context, val channels:MutableList<HighLightedChannel>, val viewType: ChannelsAdapter.ViewType) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    enum class ViewType{
        ROUND,
        NORMAL
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if(viewType == ViewType.ROUND.ordinal){
            CircleViewHolder(RoundChannelItemLayoutBinding.inflate(LayoutInflater.from(context), parent, false))
        }else{
            NormalViewHolder(ChannelItemLayoutBinding.inflate(LayoutInflater.from(context), parent, false))
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if(holder is CircleViewHolder){
            holder.onBind(channels[position])
        }else if(holder is NormalViewHolder){
            holder.onBind(channels[position])
        }
    }

    override fun getItemCount(): Int {
        return channels.size
    }

    override fun getItemViewType(position: Int): Int {
        return if(viewType==ViewType.ROUND){
            ViewType.ROUND.ordinal
        }else{
            ViewType.NORMAL.ordinal

        }

    }

    inner class CircleViewHolder(val binding: RoundChannelItemLayoutBinding) : RecyclerView.ViewHolder(binding.root) {
        fun onBind( channel: HighLightedChannel){
            channel.logo?.let {
                binding.imgProfile.setUrl(it)
            }

            channel.name?.let {
                binding.txtName.text = it
            }

            binding.txtSubscriber.visibility = View.GONE

            channel.statistics?.let {
                if(!it.hiddenSubscriberCount!!){
                    it.subscriberCount?.let {
                        binding.txtSubscriber.visibility = View.VISIBLE
                        binding.txtSubscriber.text = "Subs: ${CommonMethod.valueToUnit(it.toLong())}+"
                    }
                }
            }

            channel.chId?.let {chId->
                binding.imgProfile.setOnClickListener {
                    CommonMethod.openChannelLinkById(context, chId)
                }
            }


        }
    }
    inner class NormalViewHolder(val binding: ChannelItemLayoutBinding) : RecyclerView.ViewHolder(binding.root) {
        fun onBind( channel: HighLightedChannel){
            channel.logo?.let {
                binding.imgProfile.setUrl(it)
            }

            channel.name?.let {
                binding.txtName.text = it
            }

            binding.txtSubscriber.visibility = View.GONE

            channel.statistics?.let {
                if(!it.hiddenSubscriberCount!!){
                    it.subscriberCount?.let {
                        binding.txtSubscriber.visibility = View.VISIBLE
                        binding.txtSubscriber.text = "Subs: ${CommonMethod.valueToUnit(it.toLong())}+"
                    }
                }

                it.viewCount?.let {
                    binding.txtView.text = "Views: ${it}"
                }


                it.videoCount?.let {
                    binding.txtView.text = "Videos: ${it}"
                }
            }

            channel.chId?.let {chId->
                binding.root.setOnClickListener {
                    CommonMethod.openChannelLinkById(context, chId)
                }
            }


        }
    }
}