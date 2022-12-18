package com.sikderithub.viewsgrow.ui.generate_link

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.Window
import android.widget.*
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.github.drjacky.imagepicker.ImagePicker
import com.github.nikartm.button.FitButton
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.material.textfield.TextInputEditText
import com.google.gson.Gson
import com.nguyencse.URLEmbeddedTask
import com.permissionx.guolindev.PermissionX
import com.sikderithub.viewsgrow.Model.*
import com.sikderithub.viewsgrow.R
import com.sikderithub.viewsgrow.adapter.*
import com.sikderithub.viewsgrow.databinding.ActivityGenerateLinkBinding
import com.sikderithub.viewsgrow.repo.network.MyApi
import com.sikderithub.viewsgrow.ui.create_subdomain.SubdomainCreateActivity
import com.sikderithub.viewsgrow.ui.login.LoginActivity
import com.sikderithub.viewsgrow.ui.special_link.DomainCreateActivity
import com.sikderithub.viewsgrow.utils.*
import com.sikderithub.viewsgrow.utils.MyExtensions.setLocalImage
import com.sikderithub.viewsgrow.utils.MyExtensions.setUrl
import com.sikderithub.viewsgrow.utils.MyExtensions.shortToast
import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File

class GenerateLinkActivity : AppCompatActivity() {

    enum class DomainType{
        REGISTERED,
        SUGGESTION
    }

    enum class SubDomainType{
        REGISTERED,
        SUGGESTION
    }

    enum class Selection{
        DOMAIN,
        SUBDOMAIN,
        SUBSCRIPTION,
        DOMAIN_SUGGESTIONS
    }

    lateinit var subdomainAdapter: SubdomainAdapter
    private var subDomainDialog: Dialog? = null
    private var suffixCheckcall: Call<GenericResponse<Boolean>>? = null
    private var account: GoogleSignInAccount? = null
    private lateinit var binding : ActivityGenerateLinkBinding
    private var loadingPreview = true
    private var storeLinkData : NewFullLink? = null
    private var link : String? = null
    lateinit var myApi: MyApi
    private var isUseCustomThumb = false
    private var thumbUrl :String? = null
    lateinit var loadingDialog: LoadingDialog
    var suffixAvailable = false

    private var domainType : DomainType?  = null
    private var subDomainType : SubDomainType?  = null
    private var selection : Selection?  = null


     data class LinkMetaData(
        var loading : Boolean = true,
        var title: String? =null,
        var desc: String? =null,
        var imageUrl: String? =null,
        var errorMessage: String? = null
    )

    data class CustomThumb(
        var isUse : Boolean = false,
        var url: String? =null,
    )

    val viewModel : GenerateLinkViewModel by lazy {
        ViewModelProvider(this, GenerateLinkViewModelProvider((application as MyApp).myApi))[GenerateLinkViewModel::class.java]
    }

    private var linkMetaData : LinkMetaData = LinkMetaData()
    private var customThumb : CustomThumb = CustomThumb()


    lateinit var domainAdapter : DomainAdapter
     var subscriptionDomainAdapter : DomainAdapter? = null
     var suggestionDomainAdapter : DomainAdapter? =null

    private val domainList : MutableList<Domain> = mutableListOf()
    private val subDomainList : MutableList<Subdomain> = mutableListOf()

    private var domainSubscriptionPlan : DomainPlan? = null
    private var domainPurchasePlan : DomainPlan? = null
    private var subdomainSubscriptionPlan : DomainPlan? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGenerateLinkBinding.inflate(layoutInflater)
        setContentView(binding.root)
        loadingDialog = LoadingDialog(this)
        myApi = (application as MyApp).myApi

        link = intent?.getStringExtra(Constant.LINK)

        link?.let {
            init()
            addObserver()
            binding.txtLink.text = link

            try {
                viewModel.metaData.postValue(linkMetaData.copy(loading = true))

                val urlTask = URLEmbeddedTask {
                    // handle your code here
                    viewModel.metaData.postValue(
                        linkMetaData.copy(
                            loading = false,
                            title = it.title,
                            desc = it.description,
                            imageUrl = it.thumbnailURL,
                            errorMessage = null
                        )
                    )
                }
                urlTask.execute(link)
            }catch (e:Exception){
                viewModel.metaData.postValue(linkMetaData.copy(loading = false, errorMessage = e.message))
            }


            viewModel.getLinkGenPage(link!!)

            val type = CommonMethod.getLinkPlatform(it)
            if(type==LinkType.YOUTUBE){
                CommonMethod.getYtVideoIdFromLink(it)?.let {
                    //viewModel.getDomainSuggestion(it)
                }
            }
        }

    }

    private fun addObserver() {
        viewModel.linkRef.observe(this){state->
            state?.let {

                when(state){
                    is ScreenState.Success->{
                        loadingDialog.hide()
                        setData(it.data)

                    }
                    is ScreenState.Loading->{
                        loadingDialog.show()
                    }
                    is ScreenState.Error->{
                        loadingDialog.hide()
                        state.message?.let {
                            shortToast(it)
                        }
                    }
                }
            }

        }

        viewModel.fullLinkData.observe(this){
            it?.let {
                storeLinkData = it
                checkSuffix()
            }

        }
        viewModel.suffix.observe(this){
            it?.let {
            }
        }

        viewModel.addSubdomain.observe(this){
            it?.let { state->
                when (state){
                    is ScreenState.Success->{
                        loadingDialog.hide()
                        addNewSubdomain(state.data!!)
                        subDomainDialog?.let {it->
                            if(it.isShowing && !isFinishing){
                                it.dismiss()
                            }
                        }
                    }
                    is ScreenState.Loading->{
                        loadingDialog.show()
                    }
                    is ScreenState.Error->{
                        loadingDialog.hide()
                        state.message?.let {
                            GenericDialog.make(this)
                                .setCancelable(true)
                                .setIconType(GenericDialog.IconType.ERROR)
                                .setBodyText(it)
                                .setShowPositiveButton(true)
                                .setPositiveButtonText("Ok")
                                .setOnGenericDialogListener(object :
                                    GenericDialog.OnGenericDialogListener {
                                    override fun onPositiveButtonClick(dialog: GenericDialog?) {
                                        dialog?.hideDialog()
                                    }

                                    override fun onNegativeButtonClick(dialog: GenericDialog?) {
                                    }

                                    override fun onToast(message: String?) {
                                    }

                                })
                                .build()
                                .showDialog()
                        }
                    }
                }
            }
        }

        viewModel.suffixCheck.observe(this){
            it?.let {
                when(it){
                    is ScreenState.Success->{
                        it.data?.let {
                            if(it){
                                //available
                                binding.btnSubmit.isEnabled = true

                                suffixAvailable = true
                            }else{
                                //not available
                                binding.btnSubmit.isEnabled = false

                                suffixAvailable = false
                            }
                        }
                    }
                    is ScreenState.Loading->{
                        binding.btnSubmit.isEnabled = false
                        suffixAvailable = false

                    }
                    is ScreenState.Error->{
                        binding.btnSubmit.isEnabled = false

                    }
                }
            }
        }

        viewModel.publishLinkRes.observe(this){
            it?.let { state->
                when (state){
                    is ScreenState.Success->{
                        loadingDialog.hide()
                        state.data?.let {
                            showCopyLinkDialog(state.data)
                        }
                    }
                    is ScreenState.Loading->{
                        loadingDialog.show()
                    }
                    is ScreenState.Error->{
                        loadingDialog.hide()
                        state.message?.let {
                            shortToast(it)
                        }
                    }
                }
            }
        }

        viewModel.metaData.observe(this){

            it?.let {
                linkMetaData = it
                loadingPreview = it.loading
                Log.d("MetaData", Gson().toJson(it))
                if(it.loading && it.errorMessage==null){
                    //loading
                    Log.d("MetaData", "loading")

                    binding.layoutMeta.visibility = View.GONE
                    binding.layoutMetaLoading.visibility = View.VISIBLE
                    binding.txtMetaStatus.text = "Fetching meta data..."

                }else if(!it.loading && it.errorMessage!=null){
                    //error occurred
                    Log.d("MetaData", "error")

                    binding.layoutMeta.visibility = View.GONE
                    binding.layoutMetaLoading.visibility = View.VISIBLE
                    binding.txtMetaStatus.text = "Error Fetching meta data"

                }else if(!it.loading && it.errorMessage==null){
                    //data found
                    Log.d("MetaData", "success")

                    binding.layoutMeta.visibility = View.VISIBLE
                    binding.layoutMetaLoading.visibility = View.GONE
                    binding.txtMetaStatus.text = "Found"

                    Glide.with(this)
                        .load(it.imageUrl)
                        .into(binding.imgMeta)

                    binding.txtTitle.text = it.title
                }else{
                    Log.d("Metadata", "sf")
                }
            }
        }

        viewModel.customThumb.observe(this){
            it?.let {
                customThumb = it
                if(it.isUse && it.url!=null){

                    binding.layoutSelectThumb.visibility = View.GONE
                    binding.layoutThumb.visibility = View.VISIBLE
                    binding.imgThumb.setUrl(it.url!!)
                }
            }
        }

        viewModel.domainSuggestions.observe(this){
            it?.let { state->
                when (state){
                    is ScreenState.Success->{
                        addDomainSuggestions(it.data)
                    }
                    is ScreenState.Loading->{

                    }
                    is ScreenState.Error->{

                    }
                }
            }
        }
    }

    private fun subdomain(subdomainRes: SubdomainRes, channelName: String) {

    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private fun addDomainSuggestions(domains: List<String>?) {
        domains?.let {
            if(it.isNotEmpty()){

            }
        }
    }

    private fun addNewSubdomain(subdomain: Subdomain) {


    }

    private fun setSubdomain(subdomainRes: SubdomainRes, subdomainSugg: List<String>) {


    }


    @SuppressLint("NotifyDataSetChanged")
    private fun setData(page: LinkGenPage?) {

        page?.let { it ->

            domainPurchasePlan  = it.domainPurchasePlan
            domainSubscriptionPlan  = it.domainSubscriptionPlan
            subdomainSubscriptionPlan  = it.subDomainPlan

            var mainDomain = it.domains.find { it.main == 1 }

            if(mainDomain==null){
                mainDomain = Domain(name = "yourchannelname.link", type = "suggestion")
            }



            if(it.domains.isNotEmpty()){
                domainList.clear()
                domainList.addAll(it.domains)

                if(it.subdomainRes.subdomains.isNotEmpty()){
                    binding.layoutSubdomain.visibility = View.VISIBLE

                    subdomainAdapter = SubdomainAdapter(this, it.subdomainRes.subdomains, mainDomain, it.uniqueRef.ref)

                    subdomainAdapter.setListener(object : SubDomainSelectedListener {
                        override fun onItemSelected(subDomainSelected: SubDomainSelected) {
                            selection = Selection.SUBDOMAIN
                            domainAdapter.removeSelection()
                            suggestionDomainAdapter?.removeSelection()
                            subscriptionDomainAdapter?.removeSelection()

                            updatePublishButton()

                        }

                    })
                    binding.recySubDomain.adapter = subdomainAdapter
                }else{
                    binding.layoutSubdomain.visibility = View.GONE
                }




                domainAdapter = DomainAdapter(this, domainList)
                selection = Selection.DOMAIN
                updatePublishButton()
                domainAdapter.setListener(object : DomainSelectedListener {
                    override fun onItemSelected(selected: DomainSelected) {
                        selected.domain?.let {
                            selection = Selection.DOMAIN
                            subdomainAdapter.removeSelection()
                            subscriptionDomainAdapter?.removeSelection()
                            suggestionDomainAdapter?.removeSelection()
                            subdomainAdapter.removeSelection()



                            subdomainAdapter.updateDomain(it)
                            updatePublishButton()
                            domainAdapter.notifyDataSetChanged()

                        }
                    }

                    override fun onSuffixChange(suffix: String) {
                    }

                })

                binding.recyDomain.adapter = domainAdapter
                domainAdapter.notifyDataSetChanged()

                if(it.buyDomain.isNotEmpty()){
                    binding.layoutSubscriptionDomain.visibility = View.VISIBLE

                    subscriptionDomainAdapter = DomainAdapter(this, it.buyDomain)
                    updatePublishButton()
                    subscriptionDomainAdapter?.setListener(object : DomainSelectedListener {
                        override fun onItemSelected(domainSelected: DomainSelected) {
                            domainSelected.domain?.let {
                                selection = Selection.SUBSCRIPTION
                                subdomainAdapter.removeSelection()
                                suggestionDomainAdapter?.removeSelection()
                                domainAdapter.removeSelection()
                                subdomainAdapter.updateDomain(it)

                                updatePublishButton()
                                subscriptionDomainAdapter?.notifyDataSetChanged()

                            }
                        }

                        override fun onSuffixChange(suffix: String) {
                        }

                    })

                    binding.recyDomainSubscription.adapter = subscriptionDomainAdapter
                }else{
                    binding.layoutSubscriptionDomain.visibility = View.GONE

                }

                if(it.domainSuggestions.isNotEmpty()){
                    binding.layoutSuggestionDomain.visibility = View.VISIBLE

                    suggestionDomainAdapter = DomainAdapter(this, it.domainSuggestions)
                    updatePublishButton()
                    suggestionDomainAdapter?.setListener(object : DomainSelectedListener {
                        override fun onItemSelected(domainSelected: DomainSelected) {
                            domainSelected.domain?.let {
                                selection = Selection.DOMAIN_SUGGESTIONS
                                subdomainAdapter.removeSelection()
                                subscriptionDomainAdapter?.removeSelection()
                                domainAdapter.removeSelection()

                                subdomainAdapter.updateDomain(it)

                                updatePublishButton()

                                suggestionDomainAdapter?.notifyDataSetChanged()

                            }
                        }

                        override fun onSuffixChange(suffix: String) {
                        }

                    })

                    binding.recyDomainSuggestion.adapter = suggestionDomainAdapter
                }else{
                    binding.layoutSuggestionDomain.visibility = View.GONE
                }




            }

        }


    }

    private fun init(){
        binding.recyDomain.layoutManager = LinearLayoutManager(this)
        binding.recyDomain.setHasFixedSize(true)

        binding.recyDomainSubscription.layoutManager = LinearLayoutManager(this)
        binding.recyDomainSubscription.setHasFixedSize(true)

        binding.recyDomainSuggestion.layoutManager = LinearLayoutManager(this)
        binding.recyDomainSuggestion.setHasFixedSize(true)

        binding.recySubDomain.layoutManager = LinearLayoutManager(this)
        binding.recySubDomain.setHasFixedSize(true)


        binding.checkboxSelectThumb.setOnCheckedChangeListener {_, isChecked ->
            viewModel.customThumb.postValue(customThumb.copy(isUse= isChecked))
            if(isChecked){
                binding.layoutThumbPickerRoot.visibility = View.VISIBLE
            }else{
                binding.layoutThumbPickerRoot.visibility = View.GONE
            }
        }

        binding.layoutSelectThumb.setOnClickListener {
            checkPermission()
        }

        binding.btnSubmit.setOnClickListener {
            publishLink()
        }


    }

    private fun updatePublishButton(){

        selection?.let {
            if(it==Selection.SUBDOMAIN){

                subdomainAdapter.getSelectedItem()?.let {
                    if (it.subdomain?.userId!!.equals("0")){
                        //go to purchase
                        subdomainSubscriptionPlan?.let {
                            binding.btnSubmit.setText("Rs. ${it.price} +GST")
                        }

                    }else{
                        if(it.domain!!.type.equals("purchase")){
                            //able to publish
                            binding.btnSubmit.setText("Publish Link")
                        }else if(it.domain!!.type.equals("notpurchase")){
                            //go to subscription
                            domainSubscriptionPlan?.let {
                                binding.btnSubmit.setText("Rs. ${it.price} +GST")
                            }
                        }else{
                            //go to custom purchase
                            domainPurchasePlan?.let {
                                binding.btnSubmit.setText("Rs. ${it.price} +GST")
                            }
                        }
                    }
                }
            }else{
                //Domain selected
                when(selection){
                    Selection.SUBSCRIPTION -> {
                        domainSubscriptionPlan?.let {
                            binding.btnSubmit.setText("Rs. ${it.price} +GST")
                        }
                    }
                    Selection.DOMAIN_SUGGESTIONS -> {
                        domainPurchasePlan?.let {
                            binding.btnSubmit.setText("Rs. ${it.price} +GST")
                        }
                    }
                    else -> {
                        binding.btnSubmit.setText("Publish Your Link")
                    }
                }
            }
        }
    }


    private fun domainSuggestionCheckDialog() {
        GenericDialog.make(this)
            .setCancelable(true)
            .setBodyText("${storeLinkData?.domain} is a custom domain. Please create the domain first")
            .setPositiveButton("Continue") {
                it?.hideDialog()
                startActivity(Intent(this, DomainCreateActivity::class.java).putExtra(Constant.CUSTOM_DOMAIN, storeLinkData?.domain))

            }.setNegativeButton("Cancel") {
                it?.hideDialog()
            }
            .build()
                .showDialog()
    }

    private fun subDomainSuggestionCheckDialog() {
        GenericDialog.make(this)
            .setCancelable(true)
            .setBodyText("${storeLinkData?.subdomain} is a custom Sub Domain. Please create the Sub Domain first")
            .setPositiveButton("Continue") {
                it?.hideDialog()
                startActivity(Intent(this, SubdomainCreateActivity::class.java).putExtra(Constant.CUSTOM_SUB_DOMAIN, storeLinkData?.subdomain))

            }.setNegativeButton("Cancel") {
                it?.hideDialog()
            }
            .build()
            .showDialog()
    }


    private val launcher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == Activity.RESULT_OK) {
                if (it.data?.hasExtra(ImagePicker.EXTRA_FILE_PATH)!!) {
                    val uri = it.data?.data!!
                    imageSelectedSuccess(uri)
                }else {
                    parseError(it)
                }
            } else parseError(it)
        }

    private fun imageSelectedSuccess(uri: Uri) {



        showThumbUploadDialog(uri)
    }

    private fun showThumbUploadDialog(uri: Uri) {
        val dialog = Dialog(this)
        dialog.let { subDomainDialog->
            subDomainDialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            subDomainDialog.setCancelable(false)
            subDomainDialog.setContentView(R.layout.dialog_upload_image)

            subDomainDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            val window: Window? = subDomainDialog.window
            window?.setLayout(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            val positive = subDomainDialog.findViewById<FitButton>(R.id.btnSubmit)
            val negative = subDomainDialog.findViewById<TextView>(R.id.txtCancel)
            val thumb = subDomainDialog.findViewById<ImageView>(R.id.imgThumb)
            val progressbar = subDomainDialog.findViewById<ProgressBar>(R.id.uploadProgress)

            thumb.setLocalImage(uri)


            positive.setOnClickListener {
                val file = uri.path?.let { it1 -> File(it1) }
                file?.let {
                    val fileBody = ProgressRequestBody(file, "image/*",object :
                        ProgressRequestBody.UploadCallbacks {
                        override fun onProgressUpdate(percentage: Int) {
                            negative.isEnabled = false
                            positive.isEnabled = false

                            progressbar.isVisible.not().let {
                                progressbar.visibility = View.VISIBLE
                            }
                            progressbar.progress = percentage

                        }

                        override fun onError() {

                        }

                        override fun onFinish() {
                            Log.d("UploadThumb", "finish")
                            //progressbar.setProgress(100)
                        }

                    })

                    val filePart = MultipartBody.Part.createFormData("thumb", file.name, fileBody)

                    Coroutines.main {
                        val res = myApi.uploadThumb(filePart)

                        if(res.isSuccessful && res.body()!=null){
                            negative.isEnabled = true


                            val body = res.body()!!
                            if(!body.error){
                                progressbar.setProgress(100)
                                thumbUrl = body.data!!
                                viewModel.customThumb.postValue(customThumb.copy(url = body.data))

                                shortToast("Thumbnail Uploaded")
                                subDomainDialog.dismiss()

                            }else{
                                shortToast("Thumbnail not uploaded")
                            }
                        }
                    }

                }

            }

            negative.setOnClickListener {
                subDomainDialog.dismiss()
            }


            subDomainDialog.show()
        }
    }

    private fun parseError(activityResult: ActivityResult) {
        if (activityResult.resultCode == ImagePicker.RESULT_ERROR) {
            Toast.makeText(this, ImagePicker.getError(activityResult.data), Toast.LENGTH_SHORT)
                .show()
        } else {
            Toast.makeText(this, "Task Cancelled", Toast.LENGTH_SHORT).show()
        }
    }

    private fun checkPermission() {
        PermissionX.init(this)
            .permissions(Manifest.permission.READ_EXTERNAL_STORAGE)
            .onExplainRequestReason { scope, deniedList ->
                scope.showRequestReasonDialog(deniedList, "Core fundamental are based on these permissions", "OK", "Cancel")
            }
            .onForwardToSettings { scope, deniedList ->
                scope.showForwardToSettingsDialog(deniedList, "You need to allow necessary permissions in Settings manually", "OK", "Cancel")
            }
            .request { allGranted, grantedList, deniedList ->
                if (allGranted) {
                    pickThumb()
                } else {
                    shortToast("These permissions are denied: $deniedList")
                }
            }
    }

    private fun pickThumb() {
        launcher.launch(
            ImagePicker.with(this)
                .galleryOnly()
                .setMultipleAllowed(false)
                .crop(16f, 9f)
                .galleryMimeTypes( // no gif images at all
                    mimeTypes = arrayOf(
                        "image/png",
                        "image/jpg",
                        "image/jpeg"
                    )
                )
                .createIntent())
    }

    private fun checkSuffix(){

        if(storeLinkData==null){
            Log.d("Watcher", "null")
            return
        }



        val it = "sss" //suffinx
        if (it.toString().isEmpty()){
            viewModel.suffixCheck.postValue(ScreenState.Error(false, "Must not empty"))

            return
        }

        it?.let {
            if(it.isNotEmpty()){
                suffixCheckcall?.cancel()

                suffixCheckcall =  myApi.suffixCheck(it.toString(), storeLinkData!!.domain, storeLinkData!!.subdomain)
                viewModel.suffixCheck.postValue(ScreenState.Loading())
                suffixCheckcall!!.enqueue(object : Callback<GenericResponse<Boolean>> {
                    override fun onResponse(
                        call: Call<GenericResponse<Boolean>>,
                        response: Response<GenericResponse<Boolean>>) {

                        if(response.isSuccessful && response.body()!=null){
                            if(!response.body()!!.error){
                                viewModel.suffixCheck.postValue(ScreenState.Success(response.body()!!.data))
                                return
                            }
                        }
                        viewModel.suffixCheck.postValue(ScreenState.Error(null, "Something wrong"))


                    }

                    override fun onFailure(call: Call<GenericResponse<Boolean>>, t: Throwable) {
                        viewModel.suffixCheck.postValue(ScreenState.Error(null, "Cancel"))
                    }


                })
            }
        }



    }

    private fun publishLink() {

        selection?.let {
            if(it==Selection.SUBDOMAIN){

                subdomainAdapter.getSelectedItem()?.let {
                    if (it.subdomain?.userId!!.equals("0")){
                        //go to purchase
                        startActivity(Intent(this, SubdomainCreateActivity::class.java).putExtra(Constant.CUSTOM_SUB_DOMAIN, it.subdomain!!.name))
                    }else{
                        if(it.domain!!.type.equals("purchase")){
                            //able to publish

                            viewModel.fullLinkData.postValue(NewFullLink(it.domain!!.name, it.subdomain?.name, it.suffix, LinkType.YOUTUBE))

                            if(link!=null){
                                viewModel.addNewLink(link!!)
                            }


                        }else if(it.domain!!.type.equals("notpurchase")){
                            //go to subscription
                            startActivity(Intent(this, DomainCreateActivity::class.java)
                                .putExtra(Constant.CUSTOM_DOMAIN, it.domain?.name)
                                .putExtra(Constant.DOMAIN_PURCHASE_TYPE, "subscription")
                            )
                        }else{
                            //go to custom purchase
                            startActivity(Intent(this, DomainCreateActivity::class.java)
                                .putExtra(Constant.CUSTOM_DOMAIN, it.domain?.name)
                                .putExtra(Constant.DOMAIN_PURCHASE_TYPE, "purchase")
                            )
                        }
                    }
                }
            }else{
                //Domain selected
                when(selection){
                    Selection.SUBSCRIPTION -> {
                        subscriptionDomainAdapter?.let {
                            it.domainSelected?.let {
                                startActivity(Intent(this, DomainCreateActivity::class.java)
                                    .putExtra(Constant.CUSTOM_DOMAIN, it.domain?.name)
                                    .putExtra(Constant.DOMAIN_PURCHASE_TYPE, "subscription")
                                )
                            }
                        }
                    }
                    Selection.DOMAIN_SUGGESTIONS -> {
                        suggestionDomainAdapter?.let {
                            it.domainSelected?.let {
                                startActivity(Intent(this, DomainCreateActivity::class.java)
                                    .putExtra(Constant.CUSTOM_DOMAIN, it.domain?.name)
                                    .putExtra(Constant.DOMAIN_PURCHASE_TYPE, "purchase")
                                )
                            }
                        }
                    }
                    else -> {

                        domainAdapter?.let {
                            it.domainSelected?.let {
                                viewModel.fullLinkData.postValue(NewFullLink(it.domain?.name, null, it.domain?.ref, LinkType.YOUTUBE))
                                if(link!=null){
                                    viewModel.addNewLink(link!!)
                                }
                            }
                        }

                    }
                }

            }
        }


    }

    private fun addSubdomain() {
        if(account==null){
            showSignInDialog()
        }else{
            showAddSubdomainDialog()
        }
    }

    private fun showAddSubdomainDialog() {
        subDomainDialog = Dialog(this)
        subDomainDialog?.let {subDomainDialog->
            subDomainDialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            subDomainDialog.setCancelable(true)
            subDomainDialog.setContentView(R.layout.simple_edittext_dialog)

            subDomainDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            val window: Window? = subDomainDialog.window
            window?.setLayout(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            val subdomainInput = subDomainDialog.findViewById<TextInputEditText>(R.id.subdomainTxtInput)
            val positive = subDomainDialog.findViewById<FitButton>(R.id.btnSubmit)
            val negative = subDomainDialog.findViewById<TextView>(R.id.txtCancel)

            positive.setOnClickListener {
                val name = subdomainInput.text.toString()
                if(name.isEmpty()){
                    shortToast("Please enter subdomain name")
                    return@setOnClickListener
                }

                if(name.length>10){
                    shortToast("Subdomain must not grater than 10 character")
                    return@setOnClickListener
                }
                viewModel.addSubdomain(name)
            }

            negative.setOnClickListener {
                subDomainDialog.dismiss()
            }


            subDomainDialog.show()
        }

    }

    private fun showCopyLinkDialog(link : String) {
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(true)
        dialog.setContentView(R.layout.simple_edittext_dialog)

        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val window: Window? = dialog.window
        window?.setLayout(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        val txtTitle = dialog.findViewById<TextView>(R.id.txtDialogTittle)
        txtTitle.setText("Your Link")

        val linkInput = dialog.findViewById<TextInputEditText>(R.id.subdomainTxtInput)
        val positive = dialog.findViewById<FitButton>(R.id.btnSubmit)
        val negative = dialog.findViewById<TextView>(R.id.txtCancel)

        linkInput.hint = "Link"

        positive.setText("Copy")

        linkInput.setText(link)

        positive.setOnClickListener {
            val clipboardManager = getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
            val clipData = ClipData.newPlainText(
                "link",
                link
            )
            clipboardManager.setPrimaryClip(clipData)
            shortToast("Link Copied")
        }

        negative.setOnClickListener{
            dialog.dismiss()
            finishActivity(true)
        }


        dialog.show()
    }

    private fun showSignInDialog() {
        GenericDialog.make(this)
            .setBodyText("For Adding Subdomain You Have To Must Login First.")
            .setImage(null)
            .setIconType(GenericDialog.IconType.WARNING)
            .setCancelable(true)
            .setShowNegativeButton(true)
            .setNegativeButtonText("Cancel")
            .setPositiveButtonText("LogIn")
            .setOnGenericDialogListener(object : GenericDialog.OnGenericDialogListener {
                override fun onPositiveButtonClick(dialog: GenericDialog?) {

                    dialog?.hideDialog()
                    startActivity(Intent(this@GenerateLinkActivity, LoginActivity::class.java))

                }

                override fun onNegativeButtonClick(dialog: GenericDialog?) {
                    dialog?.hideDialog()
                }

                override fun onToast(message: String?) {

                }

            }).build()
            .showDialog()
    }

    private fun finishActivity(isLinkAdded:Boolean){
        val resultIntent = Intent()

        resultIntent.putExtra(Constant.IS_LINK_ADDEDD, isLinkAdded)
        setResult(RESULT_OK, resultIntent)
        finish()
    }

    override fun onResume() {
        super.onResume()
    }


}


class GenerateLinkViewModelProvider(private val myApi: MyApi) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(GenerateLinkViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return GenerateLinkViewModel(myApi) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}