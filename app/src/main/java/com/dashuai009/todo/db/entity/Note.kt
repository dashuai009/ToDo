package com.dashuai009.todo.db.entity


import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*


@Entity(tableName = "todo_Dao")
data class Note(
    val content: String,
    val date: Date,
    var Done: Boolean,
    val priority: Int = 2
) {
    @PrimaryKey(autoGenerate = true) var id:Int=0
    override fun toString() = " id=${id}\n Date=${date}\n Content=${content}\n Done=${Done}"
}