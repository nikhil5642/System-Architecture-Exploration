package com.appmeito.systemarchitectureexploration.mainactivity2

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.RecycledViewPool
import com.appmeito.systemarchitectureexploration.MainRepository
import com.appmeito.systemarchitectureexploration.R
import com.appmeito.systemarchitectureexploration.networking.HTTPTYPES
import com.appmeito.systemarchitectureexploration.networking.HttpClientSelector


class MainActivity2 : AppCompatActivity(){
    lateinit var viewModel2: MainViewModel2
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val mainRepository: MainRepository = MainRepository(HttpClientSelector.getClient(HTTPTYPES.HTTP11_OKHTTP))

        viewModel2=ViewModelProvider(this,MainViewModelFactory2(mainRepository))[MainViewModel2::class.java]
        val sharedViewPool=RecycledViewPool()
        sharedViewPool.setMaxRecycledViews(0,10)
        sharedViewPool.setMaxRecycledViews(1,30)
        val adapter:RecyclerViewAdapter=RecyclerViewAdapter(sharedViewPool)

        val recyclerView = findViewById<RecyclerView>(R.id.recycler_view)
        val layoutManager=LinearLayoutManager(this)
        recyclerView.layoutManager = layoutManager
        recyclerView.setRecycledViewPool(sharedViewPool)

        recyclerView.adapter=adapter

//        viewModel2.texts.observe(this
//        ) { texts ->
//            Log.d("Recycler",""+ texts.size)
//            if(texts.size>0) Log.d("Recycler",""+ texts[0])
//            adapter.setData(texts)
//        }
        viewModel2.newTexts.observe(this
        ) { paginationModel ->
            Log.d("Recycler",""+ paginationModel.data.size)
            if(paginationModel.paginationAdded== PaginationAdded.START){
                adapter.addDataStart(paginationModel.data)
                layoutManager.scrollToPositionWithOffset(layoutManager.findFirstVisibleItemPosition() + paginationModel.data.size, 0)
            }else{
                adapter.addDataEnd(paginationModel.data)
            }

        }


//        val button=findViewById<Button>(R.id.button)
//        button.setOnClickListener{ viewModel2.loadMoreData()}

    }
}
