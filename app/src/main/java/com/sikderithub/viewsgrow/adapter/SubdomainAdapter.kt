package com.sikderithub.viewsgrow.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.sikderithub.viewsgrow.Model.Domain
import com.sikderithub.viewsgrow.Model.Subdomain
import com.sikderithub.viewsgrow.R
import com.sikderithub.viewsgrow.databinding.LayoutSubdomainItemBinding




data class SubDomainSelected(
    public var position : Int? = null,
    public var domain: Domain? = null,
    public var subdomain: Subdomain? = null,
    public var suffix: String? = null
)

interface SubDomainSelectedListener{
    fun onItemSelected(subDomainSelected: SubDomainSelected)
}


class SubdomainAdapter(val context: Context, val subdomains: MutableList<Subdomain>, var domain: Domain, val suffix: String): RecyclerView.Adapter<SubdomainAdapter.MyViewHolder>() {

    var subdomainSelectedListener :SubDomainSelectedListener? = null
    var subDomainSelected : SubDomainSelected? = null
    var suffixs: MutableList<String> = mutableListOf()


    init {
        Log.d("SubDOmains", subdomains.size.toString())
        for (i in 1..subdomains.size){

            suffixs.add(suffix)
        }
    }

    fun setListener(subdomainSelectedListener : SubDomainSelectedListener){
        this.subdomainSelectedListener = subdomainSelectedListener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder(LayoutSubdomainItemBinding.inflate(LayoutInflater.from(context), parent, false))
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bind(subdomains[position], suffixs[position])
    }

    override fun getItemCount(): Int {
        return subdomains.size
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateDomain(domain: Domain){
        this.domain = domain
        notifyDataSetChanged()
    }


    @SuppressLint("NotifyDataSetChanged")
    fun removeSelection(){
        subDomainSelected = null
        notifyDataSetChanged()
    }

    fun getSelectedItem() : SubDomainSelected?{
        subDomainSelected?.let {
            it.suffix = suffixs.get(it.position!!)
            return it
        }
        return null
    }

    inner class MyViewHolder(val binding: LayoutSubdomainItemBinding):RecyclerView.ViewHolder(binding.root) {
        fun bind(subdomain: Subdomain, suff:String){
            binding.txtSubdomain.text = subdomain.name
            binding.txtDomain.text = domain.name
            binding.edtSuffix.setText(suff)


            if(subDomainSelected!=null){
                subDomainSelected!!.position?.let {selectedPos->
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
                subDomainSelected = SubDomainSelected(adapterPosition, domain, subdomains[adapterPosition], suffix)
                subdomainSelectedListener?.onItemSelected(subDomainSelected!!)
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
                    suffixs[adapterPosition] = s.toString()
                }

            })

        }
    }
}