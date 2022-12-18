package com.sikderithub.viewsgrow.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.sikderithub.viewsgrow.Model.Profile
import com.sikderithub.viewsgrow.databinding.RoundUserItemLayoutBinding
import com.sikderithub.viewsgrow.ui.profile.ProfileActivity
import com.sikderithub.viewsgrow.utils.Constant
import com.sikderithub.viewsgrow.utils.MyExtensions.setUrl

enum class ViewType{
    ROUND,
    NORMAL
}

class UserListAdapter(val context: Context, private val users: MutableList<Profile>, val viewType: ViewType) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if(viewType == ViewType.ROUND.ordinal){
              RoundViewHolder(RoundUserItemLayoutBinding.inflate(LayoutInflater.from(context), parent, false))
        }else{
            RoundViewHolder(RoundUserItemLayoutBinding.inflate(LayoutInflater.from(context), parent, false))
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if(holder is RoundViewHolder){
            holder.onBind(users[position])
        }
    }

    override fun getItemCount(): Int {
        return users.size
    }

    override fun getItemViewType(position: Int): Int {
        return if(viewType==ViewType.ROUND){
            ViewType.ROUND.ordinal
        }else{
            ViewType.ROUND.ordinal

        }

    }

    inner class RoundViewHolder(val binding: RoundUserItemLayoutBinding) : RecyclerView.ViewHolder(binding.root) {
        fun onBind( user: Profile){
            user.profilePic?.let {
                binding.imgProfile.setUrl(it)
            }

            user.fullName?.let {
                binding.txtName.text = it
            }

            binding.root.setOnClickListener {
                val intent = Intent(context, ProfileActivity::class.java)
                Constant.OTHER_USER_PROFILE = user
                intent.putExtra(Constant.OTHER_PROFILE, true)
                context.startActivity(intent)
            }
        }
    }


}
