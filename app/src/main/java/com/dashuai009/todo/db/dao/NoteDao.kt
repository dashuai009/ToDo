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
    fun all(): List<Note>
    @Query("SELECT * FROM todo_Dao")
    fun getAll(): Flow<List<Note>>

    @Query("SELECT * FROM todo_Dao WHERE id= :id  LIMIT 1")
    fun getById(id:Long):Note

    @Insert
    suspend fun insert(note: Note):Long

    @Update
    fun update(user: Note)

    @Delete
    fun delete(user: Note)
}