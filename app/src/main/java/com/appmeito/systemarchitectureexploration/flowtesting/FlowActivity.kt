package com.appmeito.systemarchitectureexploration.mainactivity2

import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.RecycledViewPool
import com.appmeito.systemarchitectureexploration.MainRepository
import com.appmeito.systemarchitectureexploration.R
import com.appmeito.systemarchitectureexploration.networking.HTTPTYPES
import com.appmeito.systemarchitectureexploration.networking.HttpClientSelector
import com.appmeito.systemarchitectureexploration.pagination.PaginationAdded
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


class FlowActivity : AppCompatActivity(){
    private lateinit var viewModel2: FlowViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val mainRepository: MainRepository = MainRepository(HttpClientSelector.getClient(HTTPTYPES.HTTP11_OKHTTP))

        viewModel2=ViewModelProvider(this,FlowViewModelFactory(mainRepository))[FlowViewModel::class.java]
        val sharedViewPool=RecycledViewPool()
        sharedViewPool.setMaxRecycledViews(0,10)
        sharedViewPool.setMaxRecycledViews(1,30)
        val adapter:FlowRecyclerViewAdapter=FlowRecyclerViewAdapter(sharedViewPool)

        val recyclerView = findViewById<RecyclerView>(R.id.recycler_view)
        val layoutManager=LinearLayoutManager(this)
        recyclerView.layoutManager = layoutManager
        recyclerView.setRecycledViewPool(sharedViewPool)
        recyclerView.adapter=adapter
        lifecycleScope.launch {
            launch{
                viewModel2.texts.collect{
                    Log.d("State Flow",""+ viewModel2.texts.value?.size)
                    adapter.setData(it)
                }
            }
           launch {
//               viewModel2.newTexts.collect{ paginationModel ->
//                   Log.d("Shared Flow",""+ viewModel2.texts.value?.size)
//                   if(paginationModel.paginationAdded== PaginationAdded.START){
//                       adapter.addDataStart(paginationModel.data)
//                       layoutManager.scrollToPositionWithOffset(layoutManager.findFirstVisibleItemPosition() + paginationModel.data.size, 0)
//                   }else{
//                       adapter.addDataEnd(paginationModel.data)
//                   }
//               }
           }

        }

        val button=findViewById<Button>(R.id.button)
        button.setOnClickListener{ viewModel2.loadMoreData()}

    }
}
