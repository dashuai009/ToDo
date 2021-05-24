package com.dashuai009.todo.db.entity

import androidx.room.TypeConverter
import java.util.*

class DateToLongConverters {
    @TypeConverter
    fun fromTimestamp(value: Long?): Date? {
        return value?.let { Date(it) }
    }

    @TypeConverter
    fun dateToTimestamp(date: Date?): Long? {
        return date?.time?.toLong()
    }


}