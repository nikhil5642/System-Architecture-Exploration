package com.appmeito.systemarchitectureexploration.mainactivity2

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.appmeito.systemarchitectureexploration.MainRepository
import com.appmeito.systemarchitectureexploration.pagination.NestedChildPaginationDataModel
import com.appmeito.systemarchitectureexploration.pagination.PaginationAdded
import com.appmeito.systemarchitectureexploration.pagination.PaginationDataModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainViewModel3(val repository: MainRepository):ViewModel() {
    val newTexts=MutableLiveData<NestedChildPaginationDataModel<List<String>>>()
    val newTextRows=MutableLiveData<PaginationDataModel<List<MutableList<String>>>>()
    val nestedTexts=MutableLiveData<MutableList<MutableList<String>>>(mutableListOf())
    var count=0
    var count2=0
    init {
        loadVerticalData(PaginationAdded.END)
    }

    private fun addVerticalText(textList: List<MutableList<String>>,pos: PaginationAdded) {
        val currentList:MutableList<MutableList<String>> = nestedTexts.value ?: mutableListOf()
        if(pos== PaginationAdded.START){
            currentList.addAll(0,textList.toMutableList())
        }else{
            currentList.addAll(textList.toMutableList())
        }
        nestedTexts.postValue(currentList)
        newTextRows.postValue(PaginationDataModel(textList,pos))
    }

    private fun addHorizontalTexts(textList: List<String>,index: Int,pos: PaginationAdded) {
        val curList:MutableList<MutableList<String>> = nestedTexts.value ?: mutableListOf()
        if(pos== PaginationAdded.START){
            curList[index].addAll(0,textList)
        }else{
            curList[index].addAll(textList)
        }
        nestedTexts.postValue(curList)
        newTexts.postValue(NestedChildPaginationDataModel(textList,index,pos))
    }

    fun loadHorizontalData(index:Int,pos: PaginationAdded){
        viewModelScope.launch (Dispatchers.IO){
            val list= mutableListOf<String>()
            for(i in 0..10){
                try {
                    val text = repository.getText()+" "+count2
                    //addText(text)
                    list.add(text)
                    count2++
                } catch (e: Exception) {
                    Log.e("MainViewModel", "Error fetching text", e)
                }
                delay(100)
            }
            addHorizontalTexts(list,index,pos)
        }
    }

    fun loadVerticalData(pos: PaginationAdded){
        viewModelScope.launch (Dispatchers.IO){
            val list= mutableListOf<MutableList<String>>()
            for(i in 0..10){
                val tempList= mutableListOf<String>()
                for(i in 0..10){
                    try {
                        val text = repository.getText()+" "+count2
                        tempList.add(text)
                        count2++
                    } catch (e: Exception) {
                        Log.e("MainViewModel", "Error fetching text", e)
                    }
                }
                list.add(tempList)
                count++
            }
            addVerticalText(list,pos)
        }
    }

}