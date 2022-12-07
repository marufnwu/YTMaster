package com.sikderithub.viewsgrow.ui.create_subdomain

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.maruf.jdialog.JDialog
import com.sikderithub.viewsgrow.BuildConfig
import com.sikderithub.viewsgrow.Model.DomainPlan
import com.sikderithub.viewsgrow.Model.Transaction
import com.sikderithub.viewsgrow.R
import com.sikderithub.viewsgrow.adapter.DomainPlanListAdapter
import com.sikderithub.viewsgrow.databinding.ActivitySubdomainCreateBinding
import com.sikderithub.viewsgrow.repo.network.MyApi
import com.sikderithub.viewsgrow.ui.special_link.DomainCreateViewModel
import com.sikderithub.viewsgrow.utils.*

class SubdomainCreateActivity : AppCompatActivity() {
    val planList: MutableList<DomainPlan> = mutableListOf()
    private lateinit var binding : ActivitySubdomainCreateBinding
    lateinit var loadingDialog: LoadingDialog
    lateinit var adapter: DomainPlanListAdapter
    val viewModel : DomainCreateViewModel by lazy { ViewModelProvider(this, MyViewModelProvider((application as MyApp).myApi))[DomainCreateViewModel::class.java] }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySubdomainCreateBinding.inflate(layoutInflater)
        setContentView(binding.root)


        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeButtonEnabled(true)
        adapter = DomainPlanListAdapter(this, planList)
        loadingDialog = LoadingDialog(this)

        intent?.let {
            it.getStringExtra(Constant.CUSTOM_SUB_DOMAIN)?.let {
                binding.chName.editText?.setText(it)
            }
        }



        initViews()

        addObserver()

        viewModel.getSubDomainPage()
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
                viewModel.requestSubDomain(chName)
                //PlayPayment(this)
                //  .lunch()

            }else{
                binding.chName.error = "Please enter your channel name"
            }
        }
    }

    private fun addObserver() {
        viewModel.getSubDomainPage
            .observe(this) {
                it?.let {
                    when (it) {
                        is ScreenState.Success -> {
                            loadingDialog.hide()
                            binding.progress.visibility = View.GONE

                            setPlanList(it.data!!.planList)
                        }
                        is ScreenState.Loading -> {
                            loadingDialog.show()
                            binding.progress.visibility = View.VISIBLE
                        }
                        is ScreenState.Error -> {
                            loadingDialog.hide()
                            binding.progress.visibility = View.GONE
                            binding.txtError.text = "Something went wrong"
                            binding.txtError.visibility = View.VISIBLE
                        }
                    }
                }
            }

        viewModel.requestSubDomain.observe(this){
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
                        JDialog.make(this)
                            .setIconType(JDialog.IconType.ERROR)
                            .setBodyText(it.message)
                            .setPositiveButton("Cancel"){
                                it.hideDialog()
                            }
                            .build()
                            .showDialog()
                    }
                }
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            android.R.id.home -> onBackPressed()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun getPaymentLink(data: Transaction?):String? {
        if(data==null){
            return null
        }

        return BuildConfig.BASE_URL+"payment/pay.php?gatewayOrderId=${data.orderId}&transactionRef=${data.reference}&type=${data.type}&gateway=${data.gateway}"
    }

    private fun setPlanList(list: List<DomainPlan>) {
        if(list.size>0) {
            binding.progress.visibility = View.GONE
            binding.txtError.visibility = View.GONE
            binding.layoutPackage.visibility = View.VISIBLE
            binding.txtPrice.text = "Price Rs. ${list.get(0).price}"
        }else{
            binding.progress.visibility = View.GONE
            binding.txtError.visibility = View.VISIBLE
            binding.layoutPackage.visibility = View.GONE

            binding.txtError.text = "No package found"
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
}