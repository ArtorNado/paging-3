package com.example.paging3sample.dp

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "book")
data class BookLocal(
    @PrimaryKey val name: String,
    val pageNumber: Int,
)