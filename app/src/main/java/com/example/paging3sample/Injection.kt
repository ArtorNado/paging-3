package com.example.paging3sample

import androidx.lifecycle.ViewModelProvider
import com.example.paging3sample.api.BookService
import com.example.paging3sample.data.repository.BookRepository
import com.example.paging3sample.presentation.ViewModelFactory

object Injection {

    private fun provideBookRepository(): BookRepository {
        return BookRepository(BookService())
    }

    fun provideViewModelFactory(): ViewModelProvider.Factory {
        return ViewModelFactory(provideBookRepository())
    }
}