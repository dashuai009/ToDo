package com.dashuai009.todo.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.dashuai009.todo.db.dao.NoteDao
import com.dashuai009.todo.db.entity.Note


@Database(entities = [Note::class], version = 1, exportSchema = false)
abstract class TodoDataBase : RoomDatabase(){
    abstract fun noteDao():NoteDao
}