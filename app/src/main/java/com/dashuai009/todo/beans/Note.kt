package com.dashuai009.todo.beans

import android.util.Log
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class Note(private var id: Long) {
    private var date: Date =Date();
    private var state: State = State.TODO;
    private var content: String = "";
    private var priority = 0;

    constructor(id: Long, date: String, state: Int, content: String, priority: Int):this(id) {

        val ft = SimpleDateFormat("EEE, d MMM yyyy HH:mm", Locale.ENGLISH)
        var t: Date? = null
        try {
            t = ft.parse(date)
        } catch (e: ParseException) {
            Log.d("parse", "Unparseable using $ft")
        }
        this.date = t!!
        this.state = State.from(state)
        this.content = content!!
        this.priority = priority
    }

    fun dateToString(): String {
        val ft = SimpleDateFormat("EEE, d MMM yyyy HH:mm", Locale.ENGLISH)
        return ft.format(date)
    }

    fun getDate(): Date {
        return date
    }
    fun getId():Long{
        return id;
    }

    fun setDate(date: Date) {
        this.date = date
    }

    fun getState(): State {
        return state
    }

    fun setState(state: State) {
        this.state = state
    }

    fun getContent(): String {
        return content
    }

    fun setContent(content: String) {
        this.content = content
    }

    fun getPriority(): Int {
        return priority
    }
}