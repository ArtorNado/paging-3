package com.example.paging3sample.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.paging3sample.data.repository.BookRepository

class ViewModelFactory(private val repository: BookRepository) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(BooksListViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return BooksListViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
