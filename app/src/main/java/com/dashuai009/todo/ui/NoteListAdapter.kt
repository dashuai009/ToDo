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


class NoteListAdapter(var context: Context, var dao: NoteDao) :
    RecyclerView.Adapter<NoteListAdapter.NoteViewHolder>() {
    var notes = mutableListOf<Note>()
    private val mSharedPreferences: SharedPreferences =
        context.getSharedPreferences("todo", Context.MODE_PRIVATE)

    fun refresh(newNotes: List<Note>) {
        notes.clear()
        notes.addAll(newNotes)
        refresh(mSharedPreferences.getBoolean(KEY_IS_NEED_SORT, false))
    }

    fun refresh(isNeedSort: Boolean) {
        if (isNeedSort) {
            notes.sortWith(Comparator { o1, o2 ->
                if (o1.Done == o2.Done) {
                    o2.priority - o1.priority
                } else {
                    if (o1.Done) {
                        1
                    } else {
                        -1
                    }
                }
            })
        } else {
            notes.sortWith(Comparator { o1, o2 ->
                (o1.date.time - o2.date.time).toInt()
            })
        }
        MainScope().launch(Dispatchers.Main) {
            notifyDataSetChanged()
        }
    }

    fun refresh(x: Note) {//这里有多线程互锁的问题。
        val iter = notes.listIterator()
        while (iter.hasNext()) {
            if (iter.next().id == x.id) {
                iter.set(x)
                return
            }
        }
        notes.add(x)
        refresh(mSharedPreferences.getBoolean(KEY_IS_NEED_SORT, false))
    }


    override fun onCreateViewHolder(parent: ViewGroup, pos: Int): NoteViewHolder {
        val itemView: View = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_note, parent, false)
        return NoteViewHolder(itemView)
    }


    internal var listener: NoteListener = context as NoteListener

    interface NoteListener {
        fun onContentClick(curNote: Note)
        //fun onDialogNegativeClick(dialog: DialogFragment)
        //fun onCancelListener(dialog: DialogFragment)
    }


    override fun onBindViewHolder(holder: NoteViewHolder, pos: Int) {
        val currentNode: Note = notes[pos]
        val dt = Date()
        val t: Long = currentNode.date.time - dt.time
        if (t <= 0) {
            holder.remainTime.text = "不剩时间了"
        } else {
            holder.remainTime.text = "还剩${ t / 1000 / 3600 }小时${t/1000/60%60}分钟"
        }
        holder.deleteBtn.setOnClickListener {
            MainScope().launch(Dispatchers.IO) {
                dao.delete(currentNode)
            }
            notes.removeAt(pos)
            refresh(
                mSharedPreferences.getBoolean(
                    KEY_IS_NEED_SORT,
                    false
                )
            )
        }
        holder.dateText.text = SIMPLE_DATE_FORMAT.format(currentNode.date)
        holder.contentText.text = currentNode.content
        holder.contentText.setOnClickListener {
            listener.onContentClick(currentNode)
        }
        holder.checkBox.isChecked = currentNode.Done
        if (currentNode.Done) {
            holder.contentText.setTextColor(Color.GRAY)
            holder.contentText.paintFlags =
                holder.contentText.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
        } else {
            holder.contentText.setTextColor(bgColor[currentNode.priority])
           // holder.contentText.setTextColor(Color.BLACK)
            holder.contentText.paintFlags =
                holder.contentText.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
        }
        holder.checkBox.setOnClickListener {
            currentNode.Done = (!currentNode.Done)

            MainScope().launch(Dispatchers.IO) {
                dao.update(currentNode)
            }
            notes[pos] = currentNode
            refresh(
                mSharedPreferences.getBoolean(
                    KEY_IS_NEED_SORT,
                    false
                )
            )
        }
    }

    override fun getItemCount(): Int {
        return notes.size;
    }

    class NoteViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val dateText: TextView = itemView.findViewById(R.id.text_date)
        val deleteBtn: View = itemView.findViewById(R.id.btn_delete)
        val contentText: TextView = itemView.findViewById(R.id.text_content)
        val checkBox: CheckBox = itemView.findViewById(R.id.checkbox)
        val remainTime: TextView = itemView.findViewById(R.id.remainTime)
    }

    companion object {
        private val SIMPLE_DATE_FORMAT = SimpleDateFormat("EEE, d MMM yyyy HH:mm", Locale.ENGLISH)
        private val bgColor = intArrayOf(Color.BLACK, Color.BLUE, Color.RED)
        private const val KEY_IS_NEED_SORT = "is_need_to_sort"
    }

}