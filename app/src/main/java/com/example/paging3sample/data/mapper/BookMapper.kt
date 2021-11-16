package com.example.paging3sample.data.mapper

import com.example.paging3sample.dp.BookLocal
import com.example.paging3sample.model.BookModel
import com.example.paging3sample.model.BookResponse

fun mapBookResponseToBookLocal(
    bookResponse: BookResponse,
    pageNumber: Int,
): BookLocal {
    return BookLocal(
        bookResponse.name,
        pageNumber
    )
}