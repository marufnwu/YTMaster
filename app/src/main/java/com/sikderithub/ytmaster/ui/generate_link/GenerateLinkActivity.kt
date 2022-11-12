package com.sikderithub.ytmaster.ui.generate_link

import android.Manifest
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
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.github.drjacky.imagepicker.ImagePicker
import com.github.nikartm.button.FitButton
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.material.textfield.TextInputEditText
import com.google.gson.Gson
import com.nguyencse.URLEmbeddedTask
import com.permissionx.guolindev.PermissionX
import com.sikderithub.ytmaster.Model.*
import com.sikderithub.ytmaster.R
import com.sikderithub.ytmaster.databinding.ActivityGenerateLinkBinding
import com.sikderithub.ytmaster.repo.network.MyApi
import com.sikderithub.ytmaster.ui.login.LoginActivity
import com.sikderithub.ytmaster.utils.*
import com.sikderithub.ytmaster.utils.MyExtensions.setLocalImage
import com.sikderithub.ytmaster.utils.MyExtensions.setUrl
import com.sikderithub.ytmaster.utils.MyExtensions.shortToast
import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File

class GenerateLinkActivity : AppCompatActivity() {
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
        }

    }

    private fun addObserver() {
        viewModel.linkRef.observe(this){state->
            when(state){
                is ScreenState.Success->{
                    loadingDialog.hide()
                    setDomains(state.data!!.domains)
                    setSubdomain(state.data.subdomainRes)
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

        viewModel.fullLinkData.observe(this){
            it?.let {
                storeLinkData = it
                checkSuffix()
                binding.txtGenLink.text = it.toString()
            }

        }
        viewModel.suffix.observe(this){
            it?.let {
                binding.edtSuffix.setText(it)
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
                            shortToast(it)
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
                                binding.imgSuffixStatus.setImageDrawable(getDrawable(R.drawable.success))
                            }else{
                                //not available
                                binding.btnSubmit.isEnabled = false

                                suffixAvailable = false
                                binding.imgSuffixStatus.setImageDrawable(getDrawable(R.drawable.error))
                            }
                        }
                    }
                    is ScreenState.Loading->{
                        binding.btnSubmit.isEnabled = false
                        suffixAvailable = false
                        Glide.with(this)
                            .load(getDrawable(R.drawable.loadin_small))
                            .into(binding.imgSuffixStatus)
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
    }

    private fun addNewSubdomain(subdomain: Subdomain) {
        binding.subdomainRadioGroup.visibility = View.VISIBLE
        binding.txtSubdomain.visibility = View.GONE

            val radioButton: RadioButton?

            radioButton = RadioButton(this)
            radioButton.text = subdomain.name
            binding.subdomainRadioGroup.addView(radioButton)

    }

    private fun setSubdomain(subdomainRes: SubdomainRes) {
        if(subdomainRes.error){
            binding.subdomainRadioGroup.visibility = View.GONE
            binding.txtSubdomain.visibility = View.VISIBLE
            binding.txtSubdomain.text = subdomainRes.msg
        }else{
            if(subdomainRes.subdomains.isEmpty()){
                binding.subdomainRadioGroup.visibility = View.GONE
                binding.txtSubdomain.visibility = View.VISIBLE
                binding.txtSubdomain.text = "You haven't add any subdomain"

            }else{
                binding.subdomainRadioGroup.visibility = View.VISIBLE
                binding.txtSubdomain.visibility = View.GONE

                var radioButton: RadioButton?
                for(subdomain in subdomainRes.subdomains){
                    radioButton = RadioButton(this)
                    radioButton.text = subdomain.name
                    binding.subdomainRadioGroup.addView(radioButton)

                }
            }
        }
    }


    private fun setDomains(domains: List<Domain>) {
        shortToast(domains.size.toString())
        var radioButton: RadioButton?
        for(domain in domains){
            radioButton = RadioButton(this)
            radioButton.text = domain.name
            binding.domainRadioGroup.addView(radioButton)
            if(domain.main==1){
                binding.domainRadioGroup.check(radioButton.id) 
            }
        }
    }

    private fun init(){
        binding.domainRadioGroup.setOnCheckedChangeListener { group, checkedId ->
            val radioButton = findViewById<RadioButton>(checkedId)


            storeLinkData?.let {
                viewModel.fullLinkData.postValue(
                    it.copy(
                        domain = radioButton.text.toString()
                    )
                )
            }
        }

        binding.subdomainRadioGroup.setOnCheckedChangeListener { group, checkedId ->
            val radioButton = findViewById<RadioButton>(checkedId)

            storeLinkData?.let {
                viewModel.fullLinkData.postValue(it.copy(subdomain = radioButton.text.toString()))
            }
        }

        binding.edtSuffix.addTextChangedListener {edt->
            Log.d("Watcher", edt.toString())

            storeLinkData?.let {
                viewModel.fullLinkData.postValue(
                    it.copy(
                        suffix = edt.toString()
                    )
                )
            }

        }

        binding.imgAddSubdomain.setOnClickListener {
            addSubdomain()
        }

        binding.btnSubmit.setOnClickListener {
            publishLink()
        }

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
                            progressbar.setProgress(percentage, true)

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



        val it = binding.edtSuffix.text
        if (it.toString().isEmpty()){
            viewModel.suffixCheck.postValue(ScreenState.Error(false, "Must not empty"))

            return
        }

        it?.let {
            if(it.toString().isNotEmpty()){
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

        if(link!=null){
            viewModel.addNewLink(link!!)
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


    override fun onStart() {
        super.onStart()
        account = GoogleSignIn.getLastSignedInAccount(this)
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