package com.dashuai009.todo.db


import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log

class TodoDbHelper(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    override fun onCreate(db: SQLiteDatabase){
        db.execSQL(TodoContract().SQL_CREATE_ENTRIES);
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL(TodoContract().SQL_CREATE_ENTRIES)
        onCreate(db)
        if (oldVersion == 1 && newVersion == 2) {
            db.execSQL("")
        }
    }

    companion object {
        // If you change the database schema, you must increment the database version.
        const val DATABASE_VERSION = 2
        const val DATABASE_NAME = "todo.db"
    }
}

