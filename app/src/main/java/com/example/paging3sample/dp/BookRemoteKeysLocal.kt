package com.example.paging3sample.dp

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "book_remote_keys")
data class BookRemoteKeysLocal(
    @PrimaryKey val bookName: String,
    val prevKey: Int?,
    val nextKey: Int?
)
