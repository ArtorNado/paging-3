package com.example.paging3sample.presentation

import androidx.lifecycle.ViewModel
import androidx.paging.PagingData
import com.example.paging3sample.data.repository.BookRepository
import com.example.paging3sample.model.BookModel
import kotlinx.coroutines.flow.Flow

class BooksListViewModel(
    private val bookRepository: BookRepository
) : ViewModel() {

    fun getBooks(): Flow<PagingData<BookModel>> {
        return bookRepository.booksFlow()
    }
}