package com.dashuai009.todo.db.dao


import com.dashuai009.todo.db.entity.Note

import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import androidx.room.Dao
import kotlinx.coroutines.flow.Flow


@Dao
interface NoteDao {
    @Query("SELECT * FROM todo_Dao")
    suspend fun all(): List<Note>
    @Query("SELECT * FROM todo_Dao")
    fun getAll(): Flow<List<Note>>

    @Insert
    suspend fun insert(note: Note):Long

    @Update
    suspend fun update(user: Note)

    @Delete
    suspend fun delete(user: Note)
}