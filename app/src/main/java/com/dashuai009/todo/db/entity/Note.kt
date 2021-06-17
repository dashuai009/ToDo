package com.dashuai009.todo.db.entity


import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*


@Entity(tableName = "todo_Dao")
data class Note(
    var content: String,
    var date: Date,
    var Done: Boolean,
    var priority: Int = 2
) {
    @PrimaryKey(autoGenerate = true) var id:Long=0
    override fun toString() = " id=${id}\n Date=${date}\n Content=${content}\n Done=${Done}\n priority=${priority}"
}