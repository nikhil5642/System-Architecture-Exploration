package com.appmeito.systemarchitectureexploration.mainactivity2

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.appmeito.systemarchitectureexploration.MainRepository
import com.appmeito.systemarchitectureexploration.pagination.PaginationAdded
import com.appmeito.systemarchitectureexploration.pagination.PaginationDataModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class FlowViewModel(val repository: MainRepository):ViewModel() {
    val texts = MutableStateFlow<MutableList<String>>(mutableListOf())
    val newTexts= MutableSharedFlow<PaginationDataModel<List<String>>>()
    var count=0
    var count2=0
    init {
        loadData()
        texts.asStateFlow()
    }

    private fun addText(text: String) {
        val currentList = texts.value
        currentList.add(text)
        texts.value=currentList
        val pag= if(count%2==0)  PaginationAdded.START else PaginationAdded.END
        viewModelScope.launch {
            newTexts.emit(PaginationDataModel(mutableListOf(text),pag))
        }
    }

    private fun addTexts(textList: List<String>) {
        val currentList = texts.value
        currentList.addAll(textList)
        texts.value=currentList
        val pag= if(count%2==0)  PaginationAdded.START else PaginationAdded.END
        viewModelScope.launch {
            newTexts.emit(PaginationDataModel(textList,pag))
        }
    }

    fun loadData(){
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
            addTexts(list)
        }
        count++
    }
    fun loadMoreData(){
        loadData()
    }
}