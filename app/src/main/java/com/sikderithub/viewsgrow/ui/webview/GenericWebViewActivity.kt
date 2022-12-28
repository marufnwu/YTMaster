package com.sikderithub.viewsgrow.ui.webview

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.webkit.*
import com.sikderithub.viewsgrow.databinding.ActivityGenericWebviewBinding
import com.sikderithub.viewsgrow.utils.BaseActivity
import com.sikderithub.viewsgrow.utils.Constant
import com.sikderithub.viewsgrow.utils.LoadingDialog


class GenericWebViewActivity : BaseActivity() {
    lateinit var binding: ActivityGenericWebviewBinding
    lateinit var loadingDialog: LoadingDialog
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGenericWebviewBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initViews()

        intent?.let {
            val pageName = it.getStringExtra(Constant.ACTIVITY_NAME);
            supportActionBar?.title = pageName
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
            supportActionBar?.setHomeButtonEnabled(true)


            val url = it.getStringExtra(Constant.WEB_URL)
            url?.let {
                binding.webView.loadUrl(it)
            }


        }



    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun initViews() {
        loadingDialog = LoadingDialog(this)

        val webview = binding.webView
        webview.webChromeClient = MyChromeWebClient()
        webview.webViewClient = MyWebCViewClient()
        webview.settings.javaScriptEnabled = true
    }

    inner class MyChromeWebClient : WebChromeClient(){
        override fun onReceivedTouchIconUrl(view: WebView?, url: String?, precomposed: Boolean) {
            super.onReceivedTouchIconUrl(view, url, precomposed)
        }



    }

    inner class MyWebCViewClient : WebViewClient(){
        override fun onPageFinished(view: WebView?, url: String?) {
            binding.progress.visibility = View.GONE
            super.onPageFinished(view, url)
        }

        override fun onReceivedError(view: WebView?, request: WebResourceRequest?, error: WebResourceError?) {
        }

        override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
            binding.progress.visibility = View.VISIBLE
            super.onPageStarted(view, url, favicon)
        }

        override fun shouldOverrideUrlLoading(
            view: WebView?,
            request: WebResourceRequest?
        ): Boolean {

            if (request!!.url.toString().startsWith("tel:") || request.url.toString().startsWith("whatsapp:")) {
                val intent = Intent(Intent.ACTION_VIEW)
                intent.data = Uri.parse(request.url.toString())
                startActivity(intent)
                return true
            }
            return false
        }




    }
}
