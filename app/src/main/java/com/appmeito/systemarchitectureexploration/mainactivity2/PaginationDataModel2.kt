package com.appmeito.systemarchitectureexploration.mainactivity2

enum class PaginationAdded{
    START,
    END
}
data class PaginationDataModel<T>(val data:T,
                               val paginationAdded: PaginationAdded
)