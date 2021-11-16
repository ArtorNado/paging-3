package com.example.paging3sample.dp

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface BookRemoteKeysDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(remoteKey: List<BookRemoteKeysLocal>)

    @Query("SELECT * FROM book_remote_keys WHERE bookName = :bookName")
    suspend fun remoteKeysBooks(bookName: String): BookRemoteKeysLocal?

    @Query("DELETE FROM book")
    suspend fun clearRemoteKeys()
}
