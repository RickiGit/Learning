package com.ricki.test.View.Fragment

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ricki.test.Adapter.UserAdapter
import com.ricki.test.R
import com.ricki.test.ViewModel.UserViewModel
import com.ricki.test.databinding.FragmentHomeBinding
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.coroutines.launch


class HomeFragment : Fragment() {

    // Initialize Binding and ViewModel Categories
    private lateinit var viewModel : UserViewModel
    private lateinit var dataBinding : FragmentHomeBinding

    var userAdapter = UserAdapter(arrayListOf())
    private lateinit var linearLayoutManager: LinearLayoutManager

    // Variable for Infinite Scroll
    val pageSize = 10
    var page = 1
    var keyword = ""

    // Variable for Checking Status Scroll
    private var previousTotal = 0
    private var loading = true
    private val visibleThreshold = 9
    var firstVisibleItem = 0
    var visibleItemCount = 0
    var totalItemCount = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        dataBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false)
        return dataBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Inflate the layout for this fragment
        viewModel = ViewModelProviders.of(this).get(UserViewModel::class.java)
        viewModel = activity?.run { ViewModelProviders.of(this).get(UserViewModel::class.java)} ?: throw Exception("Failed Activity")

        // Initiate RecyclerView
        linearLayoutManager = LinearLayoutManager(dataBinding.root.context)
        dataBinding.recyclerViewHome.layoutManager = linearLayoutManager
        dataBinding.recyclerViewHome.adapter = userAdapter

        // Create Divider RecyclerView
        var decoration = DividerItemDecoration(dataBinding.recyclerViewHome.context, linearLayoutManager.orientation)
        dataBinding.recyclerViewHome.addItemDecoration(decoration)

        // Get Keyword
        keyword = dataBinding.editTextSearch.text.toString()

        // Fetch Data from API
        viewModel.fetchUser(keyword, page, pageSize)

        // Call Method For Update Data
        lifecycleScope.launch {
            getAllUser(true)
        }
        setLoadingRequest()
        infiniteScroll()
        setStatus()
        searchUser()
    }

    fun getAllUser(isClear: Boolean){
        // Set Article to RecyclerView
        viewModel.listOfUser.observe({lifecycle},{
            userAdapter.updateData(it, isClear)
        })

        // Check Exception When Request User
        viewModel.messageException.observe({lifecycle},{
            if(it.contains("403")){
                Toast.makeText(dataBinding.root.context, "Oops, Something wrong. $it", Toast.LENGTH_LONG).show()
            }
        })
    }

    fun setLoadingRequest(){
        // Create Loading When Request Article
        viewModel.isLoading.observe({lifecycle},{
            val progressBar = dataBinding.progressBarHome
            progressBar.visibility = if (it) View.VISIBLE else View.GONE
        })
    }

    fun setStatus(){
        // Create Status When Request User
        viewModel.isStatus.observe({lifecycle},{
            val textViewStatus = dataBinding.textViewStatus
            textViewStatus.visibility = if (it) View.VISIBLE else View.GONE
        })
    }

    fun searchUser(){
        editTextSearch.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {}

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                if(s.length > 3){
                    resetInfiniteScroll()
                    keyword = s.toString()
                    viewModel.fetchUser(keyword, page, pageSize)
                    lifecycleScope.launch {
                        getAllUser(true)
                    }
                }else if(s.length == 0){
                    resetInfiniteScroll()
                    keyword = ""
                    viewModel.fetchUser(keyword, page, pageSize)
                    lifecycleScope.launch {
                        getAllUser(true)
                    }
                }
            }
        })
    }

    fun resetInfiniteScroll(){
        page = 1
        previousTotal = 0
        loading = true
        firstVisibleItem = 0
        visibleItemCount = 0
        totalItemCount = 0
    }

    fun infiniteScroll(){
        // Function for Infinite Scroll
        dataBinding.recyclerViewHome.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                visibleItemCount = dataBinding.recyclerViewHome.childCount
                totalItemCount = linearLayoutManager.itemCount
                firstVisibleItem = linearLayoutManager.findFirstVisibleItemPosition()
                if (loading) {
                    if (totalItemCount > previousTotal) {
                        loading = false
                        previousTotal = totalItemCount
                    }
                }

                if (!loading && totalItemCount - visibleItemCount <= firstVisibleItem + visibleThreshold) {
                    viewModel.fetchUser(keyword, page, pageSize)
                    lifecycleScope.launch {
                        getAllUser(false)
                    }
                    loading = true
                    page += 1
                }
            }
        })
    }
}