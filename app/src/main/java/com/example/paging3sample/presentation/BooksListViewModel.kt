package com.example.paging3sample.presentation

import androidx.lifecycle.ViewModel
import androidx.paging.PagingData
import androidx.paging.map
import com.example.paging3sample.data.repository.BookRepository
import com.example.paging3sample.model.BookModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class BooksListViewModel(
    private val bookRepository: BookRepository
) : ViewModel() {

    fun getBooks(): Flow<PagingData<BookModel>> {
        return bookRepository.booksFlow()
            .map { pagingData ->
                pagingData.map { bookLocal -> BookModel(bookLocal.name) }
            }
    }
}