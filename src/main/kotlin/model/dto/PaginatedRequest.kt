package main.kotlin.model.dto

data class PaginatedRequest<T>(
    val limit: Int,
    val page: Int,
    val content: T
)