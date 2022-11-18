package com.sikderithub.viewsgrow.ui.special_link

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.sikderithub.viewsgrow.Model.DomainPlan
import com.sikderithub.viewsgrow.adapter.DomainPlanListAdapter
import com.sikderithub.viewsgrow.databinding.ActivityDomainCreateBinding
import com.sikderithub.viewsgrow.repo.network.MyApi
import com.sikderithub.viewsgrow.utils.LoadingDialog
import com.sikderithub.viewsgrow.utils.MyApp
import com.sikderithub.viewsgrow.utils.MyExtensions.shortToast
import com.sikderithub.viewsgrow.utils.ScreenState

class DomainCreateActivity : AppCompatActivity() {
    val planList: MutableList<DomainPlan> = mutableListOf()
    private lateinit var binding : ActivityDomainCreateBinding
    lateinit var loadingDialog: LoadingDialog
    lateinit var adapter:  DomainPlanListAdapter


    val viewModel : DomainCreateViewModel by lazy { ViewModelProvider(this, MyViewModelProvider((application as MyApp).myApi))[DomainCreateViewModel::class.java] }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDomainCreateBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeButtonEnabled(true)
        adapter = DomainPlanListAdapter(this, planList)
        loadingDialog = LoadingDialog(this)



        initViews()

        addObserver()

        viewModel.getPage()

    }

    private fun initViews() {
        binding.recyPlanList.layoutManager = LinearLayoutManager(this)
        binding.recyPlanList.setHasFixedSize(true)
        adapter = DomainPlanListAdapter(this, planList)
        binding.recyPlanList.adapter = adapter


        binding.btnContinue.setOnClickListener {

            val selectedPlan = adapter.getSelectedItem
            if(selectedPlan==null){
                shortToast("Please select any plan first")
                return@setOnClickListener
            }

            val chName= binding.edtChannelName.text.toString()

            if(chName.isNotEmpty()){
                viewModel.requestDomain(chName, selectedPlan.id)
            }else{
                binding.edtChannelName.error = "Please enter your channel name"
            }
        }
    }

    private fun addObserver() {
        viewModel.getDomainPage
            .observe(this) {
                it?.let {
                    when (it) {
                        is ScreenState.Success -> {
                            loadingDialog.hide()

                            setPlanList(it.data!!.planList)
                        }
                        is ScreenState.Loading -> {
                            loadingDialog.show()
                        }
                        is ScreenState.Error -> {
                            loadingDialog.hide()
                        }
                    }
                }
            }

        viewModel.requestDomain.observe(this){
            it?.let {
                when (it) {
                    is ScreenState.Success -> {
                        loadingDialog.hide()
                    }
                    is ScreenState.Loading -> {
                        loadingDialog.show()
                    }
                    is ScreenState.Error -> {
                        loadingDialog.hide()
                    }
                }
            }
        }
    }

    private fun setPlanList(list: List<DomainPlan>) {
        planList.addAll(list)
        adapter.notifyDataSetChanged()
    }
}

class MyViewModelProvider(private val myApi: MyApi) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DomainCreateViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return DomainCreateViewModel(myApi) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}