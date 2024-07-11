package com.appmeito.systemarchitectureexploration.mainactivity2

import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.RecycledViewPool
import com.appmeito.systemarchitectureexploration.MainRepository
import com.appmeito.systemarchitectureexploration.R
import com.appmeito.systemarchitectureexploration.mainactivity2.NestedRecyclerViewAdapter.ViewHolder
import com.appmeito.systemarchitectureexploration.networking.HTTPTYPES
import com.appmeito.systemarchitectureexploration.networking.HttpClientSelector


class MainActivity3 : AppCompatActivity(),RecyclerPaginationInterface{
    lateinit var viewModel3: MainViewModel3
    lateinit var recyclerView: RecyclerView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val mainRepository: MainRepository = MainRepository(HttpClientSelector.getClient(HTTPTYPES.HTTP11_OKHTTP))

        viewModel3=ViewModelProvider(this,MainViewModelFactory3(mainRepository))[MainViewModel3::class.java]
        val sharedViewPool=RecycledViewPool()
        sharedViewPool.setMaxRecycledViews(0,10)
        sharedViewPool.setMaxRecycledViews(1,30)
        val adapter:NestedRecyclerViewAdapter=NestedRecyclerViewAdapter(this,sharedViewPool,this)

        recyclerView = findViewById<RecyclerView>(R.id.recycler_view)
        val layoutManager=LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        recyclerView.layoutManager = layoutManager
        recyclerView.setRecycledViewPool(sharedViewPool)

        recyclerView.adapter=adapter

        viewModel3.newTextRows.observe(this
        ) { paginationModel ->
            Log.d("Recycler",""+ paginationModel.data.size)
            if(paginationModel.paginationAdded== PaginationAdded.START){
                adapter.addRowStart(paginationModel.data)
                layoutManager.scrollToPositionWithOffset(layoutManager.findFirstVisibleItemPosition() + paginationModel.data.size, 0)
            }else{
                adapter.addRowEnd(paginationModel.data)
            }
        }

        viewModel3.newTexts.observe(this
        ) { paginationModel ->
            Log.d("Recycler",""+ paginationModel.data.size)
            val view=getChildRecyclerView(paginationModel.index)
            if(paginationModel.paginationAdded== PaginationAdded.START){
                adapter.addHorizontalDataStart(paginationModel.index,paginationModel.data)

                (view.adapter as ChildRecyclerViewAdapter).addDataStart(paginationModel.data)

                (view.layoutManager as LinearLayoutManager).scrollToPositionWithOffset(
                    (view.layoutManager as LinearLayoutManager)
                        .findFirstVisibleItemPosition() + paginationModel.data.size,
                    0)
            }else{
                adapter.addHorizontalDataEnd(paginationModel.index,paginationModel.data)
                (view.adapter as ChildRecyclerViewAdapter).addDataEnd(paginationModel.data)
            }
        }


        val button=findViewById<Button>(R.id.button)
        button.setOnClickListener{ loadMoreDataVertically(PaginationAdded.END)}
    }

    private fun getChildRecyclerView(position: Int):RecyclerView{
        return (recyclerView.findViewHolderForAdapterPosition(position) as ViewHolder).recyclerView
    }



    override fun loadMoreDataVertically(pos: PaginationAdded) {
        viewModel3.loadVerticalData(pos)
    }

    override fun loadMoreDataHorizontally(index: Int, pos: PaginationAdded) {
        viewModel3.loadHorizontalData(index,pos)
    }
}
interface RecyclerPaginationInterface{
    fun loadMoreDataVertically(pos: PaginationAdded)
    fun loadMoreDataHorizontally(index:Int,pos: PaginationAdded)
}