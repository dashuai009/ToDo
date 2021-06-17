package com.dashuai009.todo

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.*
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
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flowOn
import java.util.*


class MainActivity : AppCompatActivity(), NoteListAdapter.NoteListener {
    private val REQUEST_CODE_ADD = 1002
    private lateinit var binding: ActivityMainBinding
    private lateinit var notesAdapter: NoteListAdapter
    private val KEY_IS_NEED_SORT = "is_need_to_sort"
    private lateinit var dao: NoteDao
    //private  lateinit var  dbHelper:TodoDbHelper

    override fun onContentClick(curNote: Note) {
        val intent = Intent(this, NoteActivity::class.java)
        intent.putExtra("id", curNote.id)
        //intent.putExtra("dao",dao)
        this.startActivityForResult(intent, REQUEST_CODE_ADD)
    }

    private lateinit var mSharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //dbHelper=TodoDbHelper(this)
        val db = Room.databaseBuilder(
            applicationContext,
            TodoDataBase::class.java, "todo"
        ).build()
        dao = db.noteDao()

        setSupportActionBar(binding.toolbar)

        val intent = Intent(
            this,
            NoteActivity::class.java
        )
        intent.putExtra("id", (-1).toLong())

        binding.fab.setOnClickListener {
            startActivityForResult(
                intent,
                REQUEST_CODE_ADD
            )
        }

        val recyclerView: RecyclerView = findViewById(R.id.list_todo)
        recyclerView.layoutManager = LinearLayoutManager(
            this,
            LinearLayoutManager.VERTICAL, false
        )
        recyclerView.addItemDecoration(
            DividerItemDecoration(this, DividerItemDecoration.VERTICAL)
        )

        notesAdapter = NoteListAdapter(this, dao)
        mSharedPreferences = this.getSharedPreferences("todo", Context.MODE_PRIVATE)
        recyclerView.adapter = notesAdapter

        MainScope().launch(Dispatchers.IO) {
            dao.getAll().collect {
                withContext(Dispatchers.Main) {
                    notesAdapter.refresh(it)
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        notesAdapter.refresh(mSharedPreferences.getBoolean("is_need_to_sort", false))
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
                notesAdapter.refresh(false)
                true
            }
            R.id.sort_by_time -> {
                editor.putBoolean(KEY_IS_NEED_SORT, true)
                editor.apply()
                notesAdapter.refresh(true)
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
            MainScope().launch(Dispatchers.IO) {
                val tmp=dao.getById(data!!.extras!!["id"] as Long);
                notesAdapter.refresh(tmp)
            }
        }
    }


}