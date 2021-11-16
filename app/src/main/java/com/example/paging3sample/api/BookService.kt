package com.example.paging3sample.api

import com.example.paging3sample.model.BookResponse
import kotlinx.coroutines.delay
import java.io.IOException

class BookService {

    private var errorWasShown = true

    suspend fun getBooksPager(pageNumber: Int, pageSize: Int): List<BookResponse> {
        delay(3000)

        if (pageNumber == 6 && !errorWasShown) {
            errorWasShown = true

            throw IOException("Bad internet connection")
        }

        val books = mutableListOf<BookResponse>()

        if (pageNumber == 8) {
            return books
        }

        for (i in 1..pageSize) {
            books.add(BookResponse(name = "e$i/p$pageNumber/ book"))
        }

        return books
    }
}