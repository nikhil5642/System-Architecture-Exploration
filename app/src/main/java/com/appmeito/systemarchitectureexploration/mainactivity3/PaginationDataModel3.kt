package com.appmeito.systemarchitectureexploration.mainactivity3

import com.appmeito.systemarchitectureexploration.mainactivity2.PaginationAdded


data class NestedChildPaginationDataModel<T>(val data:T,
                                             val index:Int,
                               val paginationAdded: PaginationAdded
)