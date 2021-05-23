package com.dashuai009.todo.db.dao


import com.dashuai009.todo.db.entity.Note

import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import androidx.room.Dao


@Dao
interface NoteDao {
    @Query("SELECT * FROM todo_Dao")
    fun all(): Array<Note>


    @Query(
        "SELECT * FROM user WHERE first_name LIKE :first AND " +
                "last_name LIKE :last LIMIT 1"
    )
    fun findByName(first: String?, last: String?): Note?

    @Insert
    fun insert(note: Note)

    @Update
    fun update(user: Note)

    @Delete
    fun delete(user: Note)
}