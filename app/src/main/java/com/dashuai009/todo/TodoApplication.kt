package com.dashuai009.todo

import android.app.Application
import com.dashuai009.todo.db.NoteRepository
import com.dashuai009.todo.db.TodoDataBase
import com.facebook.stetho.Stetho


class TodoApplication : Application() {
    // Using by lazy so the database and the repository are only created when they're needed
    // rather than when the application starts
    val dataBase by lazy { TodoDataBase.getDatabase(this) }
    val repository by lazy { NoteRepository(dataBase.noteDao()) }
    override fun onCreate() {
        super.onCreate()
        Stetho.initializeWithDefaults(this)
    }
}

