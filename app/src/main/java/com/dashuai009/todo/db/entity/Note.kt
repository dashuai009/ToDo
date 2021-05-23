package com.dashuai009.todo.db.entity


import androidx.room.Entity
import androidx.room.PrimaryKey
import java.sql.Date


@Entity(tableName = "todo_Dao")
data class Note(
    @PrimaryKey(autoGenerate = true)val id: Long,
    val content: String,
    val date: Date,
    val Done: Int,
    val priority: Int = 2
) {
    override fun toString() =" id=${id}\n Date=${date}\n Content=${content}\n Done=${Done}"
}