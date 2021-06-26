package com.dashuai009.todo.db

import androidx.annotation.WorkerThread
import androidx.lifecycle.asFlow
import com.dashuai009.todo.db.dao.NoteDao
import com.dashuai009.todo.db.entity.Note
import kotlinx.coroutines.flow.Flow

class NoteRepository (private val noteDao: NoteDao) {

    // Room executes all queries on a separate thread.
    // Observed Flow will notify the observer when the data has changed.
    val allNotes: Flow<List<Note>> = noteDao.getAll().asFlow()

    // By default Room runs suspend queries off the main thread, therefore, we don't need to
    // implement anything else to ensure we're not doing long running database work
    // off the main thread.
    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun insert(note: Note) {
        noteDao.insert(note)
    }

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun update(note:Note){
        noteDao.update(note)
    }

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun delete(note:Note){
        noteDao.delete(note)
    }
    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun getAll():List<Note>{
         return noteDao.all()
    }
}