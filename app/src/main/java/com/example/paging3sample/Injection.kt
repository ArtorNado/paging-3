package com.example.paging3sample

import android.content.Context
import androidx.lifecycle.ViewModelProvider
import com.example.paging3sample.api.BookService
import com.example.paging3sample.data.repository.BookRepository
import com.example.paging3sample.dp.AppDatabase
import com.example.paging3sample.presentation.ViewModelFactory

object Injection {

    private fun provideBookRepository(context: Context): BookRepository {
        return BookRepository(BookService(), AppDatabase.getInstance(context))
    }

    fun provideViewModelFactory(context: Context): ViewModelProvider.Factory {
        return ViewModelFactory(provideBookRepository(context))
    }
}