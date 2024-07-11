package com.appmeito.systemarchitectureexploration.mainactivity2

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.appmeito.systemarchitectureexploration.MainRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainViewModel2(val repository: MainRepository):ViewModel() {
    val texts = MutableLiveData<MutableList<String>>(mutableListOf())
    val newTexts=MutableLiveData<PaginationDataModel<List<String>>>()
    var count=0
    var count2=0
    init {
        loadData()
    }

    private fun addText(text: String) {
        val currentList = texts.value ?: mutableListOf()
        currentList.add(text)
        texts.postValue(currentList)
        val pag= if(count%2==0)  PaginationAdded.START else PaginationAdded.END
        newTexts.postValue(PaginationDataModel(mutableListOf(text),pag))
    }

    private fun addTexts(textList: List<String>) {
        val currentList = texts.value ?: mutableListOf()
        currentList.addAll(textList)
        texts.postValue(currentList)
        val pag= if(count%2==0)  PaginationAdded.START else PaginationAdded.END
        newTexts.postValue(PaginationDataModel(textList,pag))

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