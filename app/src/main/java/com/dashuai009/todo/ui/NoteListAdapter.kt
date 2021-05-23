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
import androidx.annotation.NonNull
import androidx.recyclerview.widget.RecyclerView
import com.dashuai009.todo.NoteOperator
import com.dashuai009.todo.R
import com.dashuai009.todo.beans.Note
import com.dashuai009.todo.beans.State
import java.text.SimpleDateFormat
import java.util.*
import kotlin.Comparator
import kotlin.collections.ArrayList


class NoteListAdapter(operator: NoteOperator, context: Context) :
    RecyclerView.Adapter<NoteListAdapter.NoteViewHolder>() {
    private val operator: NoteOperator = operator
    private val notes: MutableList<Note> = ArrayList<Note>()
    private val mSharedPreferences: SharedPreferences = context.getSharedPreferences("todo", Context.MODE_PRIVATE)

    fun refresh(newNotes: List<Note>) {
        notes.clear()
        notes.addAll(newNotes)

        refresh(mSharedPreferences.getBoolean(KEY_IS_NEED_SORT, false))
    }

    fun refresh(isNeedSort: Boolean) {
        if (isNeedSort) {
            notes.sortWith(Comparator { o1, o2 ->
                if (o1.getPriority() == o2.getPriority()) {
                    o1.getState().intValue - o2.getState().intValue
                } else {
                    o1.getPriority() - o2.getPriority()
                }
            })
        }else{
            notes.sortWith(Comparator { o1, o2 ->
                (o1.getId()-o2.getId()).toInt()
            })
        }
        notifyDataSetChanged()
    }

    @NonNull
    override fun onCreateViewHolder(@NonNull parent: ViewGroup, pos: Int): NoteViewHolder {
        val itemView: View = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_note, parent, false)
        return NoteViewHolder(itemView)
    }

    override fun onBindViewHolder(@NonNull holder: NoteViewHolder, pos: Int) {
        val currentNode: Note = notes[pos]
        holder.deleteBtn.setOnClickListener {
            if (operator.deleteNote(currentNode)) {
                notes.removeAt(pos)
                refresh(
                    mSharedPreferences.getBoolean(
                        KEY_IS_NEED_SORT,
                        false
                    )
                )
            }
        }
        holder.dateText.text = SIMPLE_DATE_FORMAT.format(currentNode.getDate())
        holder.contentText.text = currentNode.getContent()
        holder.checkBox.isChecked = currentNode.getState() === State.DONE
        if (currentNode.getState() === State.DONE) {
            holder.contentText.setTextColor(Color.GRAY)
            holder.contentText.paintFlags =
                holder.contentText.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
        } else {
            holder.contentText.setTextColor(Color.BLACK)
            holder.contentText.paintFlags =
                holder.contentText.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
        }
        holder.contentText.setBackgroundColor(bgColor[currentNode.getPriority()])
        holder.checkBox.setOnClickListener {
            currentNode.setState(if (currentNode.getState() === State.DONE) State.TODO else State.DONE)
            if (operator.updateNote(currentNode)) {
                notes[pos] = currentNode
            }
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

    class NoteViewHolder(@NonNull itemView: View) : RecyclerView.ViewHolder(itemView) {
        val dateText: TextView = itemView.findViewById(R.id.text_date)
        val deleteBtn: View = itemView.findViewById(R.id.btn_delete)
        val contentText: TextView = itemView.findViewById(R.id.text_content)
        val checkBox: CheckBox = itemView.findViewById(R.id.checkbox)
    }

    companion object {
        private val SIMPLE_DATE_FORMAT = SimpleDateFormat("EEE, d MMM yyyy HH:mm", Locale.ENGLISH)
        private val bgColor = intArrayOf(Color.RED, Color.YELLOW, Color.WHITE)
        private const val KEY_IS_NEED_SORT = "is_need_sort"
    }

}