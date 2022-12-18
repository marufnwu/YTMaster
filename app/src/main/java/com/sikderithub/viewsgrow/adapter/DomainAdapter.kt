package com.sikderithub.viewsgrow.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.RecyclerView
import com.sikderithub.viewsgrow.Model.Domain
import com.sikderithub.viewsgrow.R
import com.sikderithub.viewsgrow.databinding.LayoutDomainItemBinding

data class DomainSelected(
    public var position : Int? = null,
    public var domain: Domain? = null,
    public var suffix: String? = null
)

interface DomainSelectedListener{
    fun onItemSelected(domainSelected: DomainSelected)
    fun onSuffixChange(suffix: String)
}


class DomainAdapter(val context: Context, val domains : MutableList<Domain>) : RecyclerView.Adapter<DomainAdapter.ViewHolder>() {
    var domainSelectedListener : DomainSelectedListener? = null
    var domainSelected : DomainSelected? = null

    fun setListener(domainSelectedListener : DomainSelectedListener){
        this.domainSelectedListener = domainSelectedListener
    }


    init {


        domains.indexOf(domains.find { it.main==1 }).let {
            if(it>=0){
                domainSelected = DomainSelected(
                    position = it,
                    domain = domains[it],
                    suffix = domains[it].ref
                )
            }
        }

    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutDomainItemBinding.inflate(LayoutInflater.from(context), parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(domains[position])
    }

    override fun getItemCount(): Int {
        return domains.size
    }

    @SuppressLint("NotifyDataSetChanged")
    fun removeSelection(){
        domainSelected = null
        notifyDataSetChanged()
    }


    fun getSelectedItem() : DomainSelected?{
        domainSelected?.let {
            it.suffix = it.domain!!.ref
            return it
        }
        return null
    }

    @SuppressLint("ClickableViewAccessibility")
    inner class ViewHolder(val binding:LayoutDomainItemBinding) : RecyclerView.ViewHolder(binding.root) {

        @SuppressLint("NotifyDataSetChanged")
        fun bind(domain: Domain){
            binding.txtDomain.text = domain.name
            binding.edtSuffix.setText(domain.ref)


            if(domainSelected!=null){
                domainSelected!!.position?.let {selectedPos->
                    if(selectedPos==adapterPosition){
                        binding.root.setBackgroundColor(context.resources.getColor(R.color.AliceBlue))
                        binding.checkbox.isChecked  =true

                    }else{
                        binding.root.setBackgroundColor(context.resources.getColor(R.color.transparent))
                        binding.checkbox.isChecked  =false

                    }
                }
            }else{
                binding.checkbox.isChecked  =false
                binding.root.setBackgroundColor(context.resources.getColor(R.color.transparent))
            }

            binding.checkbox.setOnClickListener {
                val suffix  = binding.edtSuffix.text.toString()
                domainSelected = DomainSelected(adapterPosition, domains[adapterPosition], suffix)
                domainSelectedListener?.onItemSelected(domainSelected!!)
            }

            binding.edtSuffix.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {

                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

                }

                override fun afterTextChanged(s: Editable?) {
                    val suffix = s.toString()
                    domains[position].ref = suffix
                }

            })

        }
    }

}