package com.appmeito.systemarchitectureexploration.mainactivity2

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.RecycledViewPool
import com.appmeito.systemarchitectureexploration.R
import com.appmeito.systemarchitectureexploration.mainactivity3.RecyclerPaginationInterface
import com.appmeito.systemarchitectureexploration.pagination.PaginationAdded

class NestedRecyclerViewAdapter(val context: Context,val viewPool: RecycledViewPool, val listener: RecyclerPaginationInterface):
    RecyclerView.Adapter<NestedRecyclerViewAdapter.ViewHolder>() {

        private val dataset = mutableListOf<MutableList<String>>()

        class ViewHolder(view:View): RecyclerView.ViewHolder(view) {
            val recyclerView:RecyclerView = view.findViewById(R.id.child_recycler_view)
            val childButton:Button=view.findViewById(R.id.child_button)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view=LayoutInflater.from(parent.context).inflate(R.layout.child_recycler_view,
                parent,false)

            return ViewHolder(view)
        }

        override fun getItemCount(): Int {
            return dataset.size
        }

    override fun getItemViewType(position: Int): Int {
        return 0
    }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            if (holder.recyclerView.layoutManager == null) {
                val layoutManager =
                    LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
                holder.recyclerView.setLayoutManager(layoutManager)
            }

            var adapter: RecyclerView.Adapter<*>? = holder.recyclerView.getAdapter()
            if (adapter == null || adapter !is ChildRecyclerViewAdapter) {
                adapter = ChildRecyclerViewAdapter()
                adapter.setData(dataset[position])
                holder.recyclerView.setAdapter(adapter)
                holder.recyclerView.setRecycledViewPool(viewPool)
            } else {
                (adapter as ChildRecyclerViewAdapter).setData(dataset[position])
            }

            holder.childButton.setOnClickListener{
                listener.loadMoreDataHorizontally(position, PaginationAdded.START)
            }
        }

        fun addRowStart(newTexts: List<MutableList<String>>){
            dataset.addAll(0,newTexts)
            notifyDataSetChanged()
        }
        fun addRowEnd(newTexts: List<MutableList<String>>){
            dataset.addAll(0,newTexts)
            notifyItemRangeChanged(dataset.size-newTexts.size,newTexts.size)
        }

        fun addHorizontalDataStart(index:Int,newTexts: List<String>){
            dataset[index].addAll(0,newTexts)
        }


        fun addHorizontalDataEnd(index: Int,newTexts: List<String>){
            dataset[index].addAll(newTexts)
        }

}