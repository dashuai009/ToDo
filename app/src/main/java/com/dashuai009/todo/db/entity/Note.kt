package com.dashuai009.todo.db.entity


import android.os.Parcel
import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*


@Entity(tableName = "todo_Dao")
data class Note(
    var content: String="",
    var date: Date = Date(),
    var Done: Boolean = false,
    var priority: Int = 2
) : Parcelable {
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0

    constructor(parcel: Parcel) : this(
        parcel.readString().toString(),
        Date(parcel.readLong()),
        parcel.readByte() != 0.toByte(),
        parcel.readInt()
    ) {
        id = parcel.readLong()
    }

    override fun toString() =
        " id=${id}\n Date=${date}\n Content=${content}\n Done=${Done}\n priority=${priority}"

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(content)
        parcel.writeLong(date.time)
        parcel.writeByte(if (Done) 1 else 0)
        parcel.writeInt(priority)
        parcel.writeLong(id)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Note> {
        override fun createFromParcel(parcel: Parcel): Note {
            return Note(parcel)
        }

        override fun newArray(size: Int): Array<Note?> {
            return arrayOfNulls(size)
        }
    }
}