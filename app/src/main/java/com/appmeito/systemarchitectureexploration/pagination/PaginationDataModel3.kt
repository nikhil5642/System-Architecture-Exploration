package com.appmeito.systemarchitectureexploration.pagination


data class NestedChildPaginationDataModel<T>(val data:T,
                                             val index:Int,
                               val paginationAdded: PaginationAdded
)