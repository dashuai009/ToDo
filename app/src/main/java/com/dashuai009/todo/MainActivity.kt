package com.dashuai009.todo

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.ui.AppBarConfiguration
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.dashuai009.todo.databinding.ActivityMainBinding
import com.dashuai009.todo.db.TodoDataBase
import com.dashuai009.todo.db.dao.NoteDao
import com.dashuai009.todo.db.entity.Note
import com.dashuai009.todo.ui.NoteListAdapter
import com.dashuai009.todo.ui.NoteViewModel
import com.dashuai009.todo.ui.NoteViewModelFactory
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flowOn
import java.util.*


class MainActivity : AppCompatActivity(), NoteListAdapter.NoteListener {
    private val REQUEST_CODE_ADD = 1002
    private lateinit var binding: ActivityMainBinding
    private lateinit var notesAdapter: NoteListAdapter
    private val KEY_IS_NEED_SORT = "is_need_to_sort"
    private lateinit var mSharedPreferences: SharedPreferences

    private val noteViewModel: NoteViewModel by viewModels {
        NoteViewModelFactory((application as TodoApplication).repository)
    }

    override fun onContentClick(curNote: Note) {
        val intent = Intent(this, NoteActivity::class.java)
        intent.putExtra("note", curNote)
        this.startActivityForResult(intent, REQUEST_CODE_ADD)
    }

    override fun onCheckBoxClick(curNote: Note) {
        curNote.Done = !curNote.Done
        noteViewModel.update(curNote)
    }

    override fun onDeleteBtnClick(curNote: Note) {
        noteViewModel.delete(curNote)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)


        setSupportActionBar(binding.toolbar)

        val intent = Intent(
            this,
            NoteActivity::class.java
        )
        intent.putExtra("note", Note())

        binding.fab.setOnClickListener {
            startActivityForResult(
                intent,
                REQUEST_CODE_ADD
            )
        }

        val recyclerView = findViewById<RecyclerView>(R.id.list_todo)
        notesAdapter = NoteListAdapter(this)
        recyclerView.adapter = notesAdapter
        recyclerView.layoutManager = LinearLayoutManager(
            this,
            LinearLayoutManager.VERTICAL, false
        )
        /*recyclerView.addItemDecoration(
            DividerItemDecoration(this, DividerItemDecoration.VERTICAL)
        )*/
        mSharedPreferences = this.getSharedPreferences("todo", Context.MODE_PRIVATE)

        noteViewModel.allNotes.observe(this) { notes ->
            refresh(notes)
        }

    }

    fun refresh(notes:List<Note>){
        val mut_notes = mutableListOf<Note>()
        mut_notes.addAll(notes)
        if (mSharedPreferences.getBoolean(KEY_IS_NEED_SORT, false)) {
            mut_notes.sortWith(Comparator { o1, o2 ->
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
            mut_notes.sortWith(Comparator { o1, o2 ->
                (o1.date.time - o2.date.time).toInt()
            })
        }
        mut_notes.let {
            notesAdapter.submitList(it)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val editor = mSharedPreferences.edit()
        return when (item.itemId) {
            R.id.normally_sort -> {
                editor.putBoolean(KEY_IS_NEED_SORT, false)
                editor.apply()
                MainScope().launch(Dispatchers.IO) {
                    refresh( noteViewModel.getAll())
                }
                true
            }
            R.id.sort_by_time -> {
                editor.putBoolean(KEY_IS_NEED_SORT, true)
                editor.apply()
                MainScope().launch(Dispatchers.IO) {
                    refresh( noteViewModel.getAll())
                }
                true
            }
            else -> {
                super.onOptionsItemSelected(item)
            }
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_ADD && resultCode == RESULT_OK) {

            val cur: Note = data!!.extras!!["curNode"] as Note
            Log.d("tagtagtag", cur.toString())
            if (cur.id == (0).toLong()) {
                noteViewModel.insert(cur)
            } else {
                noteViewModel.update(cur)
            }

        }
    }


}