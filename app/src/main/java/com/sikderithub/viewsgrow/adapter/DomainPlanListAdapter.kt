package com.sikderithub.viewsgrow.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.sikderithub.viewsgrow.Model.DomainPlan
import com.sikderithub.viewsgrow.R
import com.sikderithub.viewsgrow.databinding.LayoutDomainPlanBinding

class DomainPlanListAdapter(val context: Context, val plans : List<DomainPlan>) : RecyclerView.Adapter<DomainPlanListAdapter.MyViewHolder>() {
    var selected = -1;

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder(LayoutDomainPlanBinding.inflate(LayoutInflater.from(context),  parent, false))
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.onBind(plans[position])
    }

    override fun getItemCount(): Int {
        return plans.size
    }

    val getSelectedItem : DomainPlan?
        get() {
            if(selected>=0){
                return plans[selected]
            }

            return null
        }

    inner class MyViewHolder(private val binding: LayoutDomainPlanBinding) : RecyclerView.ViewHolder(binding.root) {
        fun onBind(plan: DomainPlan){


            binding.txtValidity.text = "${plan.validity} Days"
            binding.txtPrice.text = plan.price.toString()

            if(selected == adapterPosition){
                binding.root.background  = context.resources.getDrawable(R.drawable.round_primary_variant_border_with_bg)
            }else{
                binding.root.background  = context.resources.getDrawable(R.drawable.round_pripary_variant_border)
            }


            binding.root.setOnClickListener {
                selected = adapterPosition
                notifyDataSetChanged()
            }
        }
    }
}