package com.dashuai009.todo.ui

import android.content.Context
import android.content.SharedPreferences
import android.graphics.Color
import android.graphics.Paint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.dashuai009.todo.R
import com.dashuai009.todo.db.dao.NoteDao
import com.dashuai009.todo.db.entity.Note
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import kotlin.Comparator
import com.dashuai009.todo.ui.NoteListAdapter.NoteViewHolder


class NoteListAdapter(private val context: Context) :
    ListAdapter<Note, NoteViewHolder>(NOTES_COMPARATOR) {
    internal var listener: NoteListener = context as NoteListener

    interface NoteListener {
        fun onContentClick(curNote: Note)
        fun onCheckBoxClick(curNote: Note)
        fun onDeleteBtnClick(curNote: Note)
    }

    override fun onCreateViewHolder(parent: ViewGroup, pos: Int): NoteViewHolder {
        val itemView: View = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_note, parent, false)
        return NoteViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: NoteViewHolder, pos: Int) {
        val curNote = getItem(pos)
        holder.bind(curNote)
        holder.deleteBtn.setOnClickListener {
            listener.onDeleteBtnClick(curNote)
        }
        holder.contentText.setOnClickListener {
            listener.onContentClick(curNote)
        }
        holder.checkBox.setOnClickListener {
            listener.onCheckBoxClick(curNote)
        }

    }

    class NoteViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val dateText: TextView = itemView.findViewById(R.id.text_date)
        val deleteBtn: View = itemView.findViewById(R.id.btn_delete)
        val contentText: TextView = itemView.findViewById(R.id.text_content)
        val checkBox: CheckBox = itemView.findViewById(R.id.checkbox)
        val remainTime: TextView = itemView.findViewById(R.id.remainTime)

        fun bind(curNote: Note) {
            //dateText
            dateText.text = SIMPLE_DATE_FORMAT.format(curNote.date)
            //deleteBtn  above

            //contentText
            contentText.text = curNote.content

            //checkBox
            checkBox.isChecked = curNote.Done
            if (curNote.Done) {
                contentText.setTextColor(Color.GRAY)
                contentText.paintFlags =
                    contentText.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
            } else {
                contentText.setTextColor(bgColor[curNote.priority])
                // holder.contentText.setTextColor(Color.BLACK)
                contentText.paintFlags =
                    contentText.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
            }

            //remainTime
            val dt = Date()
            val t: Long = curNote.date.time - dt.time
            if (t <= 0) {
                remainTime.text = "不剩时间了"
            } else {
                remainTime.text =
                    "还剩${t / 1000 / 60 / 60 / 24}天${t % (3600 * 1000 * 24) / 1000 / 60 / 60}小时${t / 1000 / 60 % 60}分钟"
            }
        }
    }

    companion object {
        private val SIMPLE_DATE_FORMAT = SimpleDateFormat("EEE, d MMM yyyy HH:mm", Locale.ENGLISH)
        private val bgColor = intArrayOf(Color.BLACK, Color.BLUE, Color.RED)
        private val NOTES_COMPARATOR = object : DiffUtil.ItemCallback<Note>() {
            override fun areItemsTheSame(oldItem: Note, newItem: Note): Boolean {
                return oldItem === newItem
            }

            override fun areContentsTheSame(oldItem: Note, newItem: Note): Boolean {
                return oldItem.content == newItem.content
            }
        }


    }

}