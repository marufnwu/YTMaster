package com.sikderithub.viewsgrow.ui.special_link

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.sikderithub.viewsgrow.Model.DomainPlan
import com.sikderithub.viewsgrow.Model.Transaction
import com.sikderithub.viewsgrow.R
import com.sikderithub.viewsgrow.adapter.DomainPlanListAdapter
import com.sikderithub.viewsgrow.databinding.ActivityDomainCreateBinding
import com.sikderithub.viewsgrow.repo.network.MyApi
import com.sikderithub.viewsgrow.ui.login.LoginActivity
import com.sikderithub.viewsgrow.utils.*
import com.sikderithub.viewsgrow.utils.MyExtensions.shortToast

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

        intent?.let {
            it.getStringExtra(Constant.CUSTOM_DOMAIN)?.let {
                binding.chName.editText?.setText(it)
            }
        }



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

            if(!MyApp.isLogged()){
                CommonMethod.showLoginDialog(context = this)
                return@setOnClickListener
            }

            val selectedPlan = adapter.getSelectedItem


            val chName= binding.chName.editText?.text.toString()

            if(chName.isNotEmpty()){
                viewModel.requestDomain(chName)
            }else{
                binding.chName.error = "Please enter your channel name"
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
                        getPaymentLink(it.data)?.let {
                            CommonMethod.openLink(it, this)
                        }

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

    private fun getPaymentLink(data: Transaction?):String? {
        if(data==null){
            return null
        }

        return "http://192.168.0.101/ytvideos/payment/pay.php?transactionRef=${data.reference}&type=${data.type}&gateway=${data.gateway}"
    }

    private fun setPlanList(list: List<DomainPlan>) {
        planList.addAll(list)
        adapter.notifyDataSetChanged()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_domain_request, menu)

        return true;
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.menuRequestList-> domainRequestList()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun domainRequestList() {
        if(MyApp.isLogged()){
            
        }else{
            CommonMethod.showLoginDialog(context = this)
        }
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