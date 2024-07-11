package com.appmeito.systemarchitectureexploration.mainactivity2

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.compose.foundation.rememberScrollState
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.RecycledViewPool
import com.appmeito.systemarchitectureexploration.R

class ChildRecyclerViewAdapter():
    RecyclerView.Adapter<ChildRecyclerViewAdapter.ViewHolder>() {

        private val dataset = mutableListOf<String>("a","b","x")

        class ViewHolder(view:View): RecyclerView.ViewHolder(view) {
            val textView:TextView = view.findViewById(R.id.text)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view=LayoutInflater.from(parent.context).inflate(R.layout.item_simple,
                parent,false)

            return ViewHolder(view)
        }

        override fun getItemCount(): Int {
            return dataset.size
        }

        override fun getItemViewType(position: Int): Int {
            return 1
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
           holder.textView.text=dataset[position]
        }

        fun addDataStart(newTexts: List<String>){
            dataset.addAll(0,newTexts)
            notifyDataSetChanged()
        }

        fun addDataEnd(newTexts: List<String>){
            dataset.addAll(newTexts)
            notifyItemRangeChanged(dataset.size-newTexts.size,newTexts.size)
        }

        fun setData(newTexts: List<String>) {
            dataset.clear()
            dataset.addAll(newTexts)
            notifyDataSetChanged()
        }
}