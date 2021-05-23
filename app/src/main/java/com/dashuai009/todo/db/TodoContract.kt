package com.dashuai009.todo.db

import android.provider.BaseColumns

public class TodoContract {
    val SQL_CREATE_ENTRIES: String = "CREATE TABLE ${NoteEntry.TABLE_NAME} (" +
            "${BaseColumns._ID}  INTEGER PRIMARY KEY ,  " +
            "${NoteEntry.COLUMN_NAME_DATE}  TEXT,  " +
            "${NoteEntry.COLUMN_NAME_STATE}   INTEGER," +
            "${NoteEntry.COLUMN_NAME_PRIORITY}   INTEGER, " +
            "${NoteEntry.COLUMN_NAME_CONTENT}   TEXT)"
    public val SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS ${NoteEntry.TABLE_NAME}"
    val SQL_FIND_BY_ID =
        "SELECT * FROM ${NoteEntry.TABLE_NAME} WHERE  ${BaseColumns._ID} = ?"

    // To prevent someone from accidentally instantiating the contract class,
    // make the constructor private.
    private fun TodoContract() {}

    /* Inner class that defines the table contents */
    object NoteEntry : BaseColumns {
        const val TABLE_NAME = "TodoList"
        const val COLUMN_NAME_DATE = "Date"
        const val COLUMN_NAME_STATE = "State"
        const val COLUMN_NAME_CONTENT = "Content"
        const val COLUMN_NAME_PRIORITY = "Priority"
    }
}