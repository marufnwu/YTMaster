package com.sikderithub.ytmaster.ui.all_link

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.sikderithub.ytmaster.Model.Link
import com.sikderithub.ytmaster.adapter.LinkListAdapter
import com.sikderithub.ytmaster.databinding.ActivityAllLinkBinding
import com.sikderithub.ytmaster.repo.network.MyApi
import com.sikderithub.ytmaster.utils.MyApp
import com.sikderithub.ytmaster.utils.ScreenState

class AllLinkActivity : AppCompatActivity() {
    lateinit var layoutManager: LinearLayoutManager
    lateinit var binding: ActivityAllLinkBinding
    var pastVisibleItem : Int =0
    var visibleItemCount = 0
    var totalItemCount=0


    private var CURR_PAGE = 1
    private var TOTAL_PAGE = 0
    var isLoading = true

    lateinit var thumbLinkAdapter : LinkListAdapter
    var thumbLinkList = mutableListOf<Link>()


    val viewModel : AllLinkViewModel by lazy {
        ViewModelProvider(this, ViewModelProvider((application as MyApp).myApi))[AllLinkViewModel::class.java]
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAllLinkBinding.inflate(layoutInflater)
        setContentView(binding.root)


        thumbLinkAdapter = LinkListAdapter(this, thumbLinkList ,LinkListAdapter.ViewType.THUMBNAIL)


        initView()
        initObserver()

    }

    private fun initObserver() {
        viewModel.thumbLinks.observe(this){

            it?.let {
                when(it){
                    is ScreenState.Success ->{
                        isLoading = false
                        binding.recyLink.visibility = View.VISIBLE
                        binding.progress.visibility =View.GONE



                        it.data?.let {
                            it.maindata?.let {
                                thumbLinkList.addAll(it)
                                thumbLinkAdapter.notifyDataSetChanged()


                            }

                            TOTAL_PAGE = it.totalPage!!
                        }
                    }
                    is ScreenState.Loading ->{
                        if(CURR_PAGE==1){
                            binding.progress.visibility =View.VISIBLE
                        }
                    }
                    is ScreenState.Error ->{
                        binding.progress.visibility =View.GONE
                        isLoading = false
                    }
                }
            }
        }

    }

    private fun initView() {
        layoutManager  = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding.recyLink.layoutManager = layoutManager
        binding.recyLink.setHasFixedSize(true)
        initRecyScrollListener()

        //binding.recyLink.adapter = linkAdapter

        binding.recyLink.adapter = thumbLinkAdapter

    }

    private fun initRecyScrollListener() {

        binding.recyLink.addOnScrollListener(
            object: RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)
                    visibleItemCount = layoutManager.getChildCount()
                    totalItemCount = layoutManager.getItemCount()
                    pastVisibleItem = layoutManager.findFirstVisibleItemPosition()

                    if (dy > 0) {
                        Log.d("Pagination", "Scrolled")
                        if (!isLoading) {
                            if (CURR_PAGE <= TOTAL_PAGE) {
                                Log.d("Pagination", "Total Page $TOTAL_PAGE")
                                Log.d("Pagination", "Page $CURR_PAGE")
                                if (visibleItemCount + pastVisibleItem >= totalItemCount) {
                                    //postListProgress.setVisibility(View.VISIBLE)
                                    isLoading = true
                                    Log.v("...", "Last Item Wow !")
                                    //Do pagination.. i.e. fetch new data
                                    CURR_PAGE++
                                    if (CURR_PAGE <= TOTAL_PAGE) {
                                        viewModel.getThumbLinks(currPage = CURR_PAGE, totalPage = TOTAL_PAGE)
                                    } else {
                                        isLoading = false
                                        //postListProgress.setVisibility(View.GONE)
                                    }
                                }
                            } else {

                                //postListProgress.setVisibility(View.GONE);
                                Log.d("Pagination", "End of page")
                            }
                        } else {
                            Log.d("Pagination", "Loading")
                        }
                    }
                }
            })
    }
}

class ViewModelProvider(private val myApi: MyApi) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AllLinkViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return AllLinkViewModel(myApi) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}