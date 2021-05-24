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
import com.dashuai009.todo.activity.DebugActivity
import com.dashuai009.todo.activity.SettingActivity
import com.dashuai009.todo.databinding.ActivityMainBinding
import com.dashuai009.todo.db.TodoDataBase
import com.dashuai009.todo.db.dao.NoteDao
import com.dashuai009.todo.db.entity.Note
import com.dashuai009.todo.ui.NoteListAdapter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch


class MainActivity : AppCompatActivity() , CoroutineScope by MainScope() {
    private val REQUEST_CODE_ADD = 1002
    private lateinit var binding: ActivityMainBinding
    private lateinit var notesAdapter: NoteListAdapter

    private lateinit var dao: NoteDao
    //private  lateinit var  dbHelper:TodoDbHelper


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

        notesAdapter = NoteListAdapter(object : NoteOperator {
            override suspend fun deleteNote(note: Note) {
                 this@MainActivity.deleteNote(note)
            }

            override suspend fun updateNote(note: Note) {
                 this@MainActivity.updateNode(note)
            }
        }, baseContext)
        mSharedPreferences = baseContext.getSharedPreferences("todo", Context.MODE_PRIVATE)
        recyclerView.adapter = notesAdapter
        launch {
            notesAdapter.refresh(loadNotesFromDatabase())
        }
    }

    override fun onResume() {
        super.onResume()
        notesAdapter.refresh(mSharedPreferences.getBoolean("is_need_sort", false))
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> {
                intent.setClass(this, SettingActivity::class.java)
                startActivity(intent)
                true
            }
            R.id.action_debug -> {
                intent.setClass(this, DebugActivity::class.java)
                startActivity(intent)
                true
            }
            else -> {
                super.onOptionsItemSelected(item)
            }
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_ADD
            && resultCode == RESULT_OK
        ) {
            launch {
                notesAdapter.refresh(loadNotesFromDatabase())
            }
        }
    }

    private suspend fun loadNotesFromDatabase(): List<Note> {
        return dao.all()
    }

    private suspend fun deleteNote(note: Note): Boolean {
        dao.delete(note)
        return true
    }

    private suspend fun updateNode(note: Note): Boolean {
        dao.update(note)
        return true
    }

}