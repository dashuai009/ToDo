package com.dashuai009.todo

import com.dashuai009.todo.beans.Note

interface NoteOperator {
    fun deleteNote(note: Note):Boolean

    fun updateNote(note: Note):Boolean
}