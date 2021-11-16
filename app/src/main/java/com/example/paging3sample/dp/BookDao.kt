package com.example.paging3sample.dp

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface BookDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(books: List<BookLocal>)

    @Query("SELECT * FROM book ORDER BY pageNumber ASC")
    fun booksPaging(): PagingSource<Int, BookLocal>

    @Query("DELETE FROM book")
    suspend fun clearBooks()
}