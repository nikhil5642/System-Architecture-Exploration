package com.appmeito.systemarchitectureexploration.pagination

enum class PaginationAdded{
    START,
    END
}
data class PaginationDataModel<T>(val data:T,
                               val paginationAdded: PaginationAdded
)