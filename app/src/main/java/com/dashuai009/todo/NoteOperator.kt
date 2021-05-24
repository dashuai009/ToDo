package com.dashuai009.todo

import com.dashuai009.todo.db.entity.Note


interface NoteOperator {
    suspend fun deleteNote(note: Note)

    suspend fun updateNote(note: Note)
}