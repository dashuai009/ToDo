package com.dashuai009.todo.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.dashuai009.todo.db.dao.NoteDao
import com.dashuai009.todo.db.entity.DateToLongConverters
import com.dashuai009.todo.db.entity.Note


@Database(entities = [Note::class], version = 1, exportSchema = false)
@TypeConverters(DateToLongConverters::class)
abstract class TodoDataBase : RoomDatabase(){
    abstract fun noteDao():NoteDao

    companion object {
        // Singleton prevents multiple instances of database opening at the
        // same time.
        @Volatile
        private var INSTANCE: TodoDataBase? = null

        fun getDatabase(context: Context): TodoDataBase {
            // if the INSTANCE is not null, then return it,
            // if it is, then create the database
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    TodoDataBase::class.java,
                    "todo"
                ).build()
                INSTANCE = instance
                // return instance
                instance
            }
        }
    }
}