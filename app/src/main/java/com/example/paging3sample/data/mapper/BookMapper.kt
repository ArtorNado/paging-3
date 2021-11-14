package com.example.paging3sample.data.mapper

import com.example.paging3sample.model.BookModel
import com.example.paging3sample.model.BookResponse

fun mapBookResponseToBookModel(
    bookResponse: BookResponse
): BookModel {
    return BookModel(
        bookResponse.name
    )
}