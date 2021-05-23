package com.dashuai009.todo

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
import android.provider.BaseColumns
import android.util.Log
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.ui.AppBarConfiguration
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dashuai009.todo.activity.DebugActivity
import com.dashuai009.todo.activity.SettingActivity
import com.dashuai009.todo.beans.Note
import com.dashuai009.todo.databinding.ActivityMainBinding
import com.dashuai009.todo.db.TodoContract.NoteEntry
import com.dashuai009.todo.db.TodoDbHelper
import com.dashuai009.todo.ui.NoteListAdapter

class MainActivity : AppCompatActivity() {
    private val REQUEST_CODE_ADD = 1002
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding
    private lateinit var notesAdapter: NoteListAdapter
    private  lateinit var  dbHelper:TodoDbHelper
    private lateinit var mSharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        dbHelper=TodoDbHelper(this)

        setSupportActionBar(binding.toolbar)

        val intent = Intent(
            this,
            NoteActivity::class.java
        );

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
            override fun deleteNote(note: Note): Boolean {
                return this@MainActivity.deleteNote(note)
            }

            override fun updateNote(note: Note): Boolean {
                return this@MainActivity.updateNode(note)
            }
        }, baseContext)
        mSharedPreferences= baseContext.getSharedPreferences("todo", Context.MODE_PRIVATE)
        recyclerView.adapter = notesAdapter

        notesAdapter.refresh(loadNotesFromDatabase())
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
                intent.setClass(this,SettingActivity::class.java)
                startActivity(intent)
                true
            }
            R.id.action_debug -> {
                intent.setClass(this,DebugActivity::class.java)
                startActivity(intent)
                true
            }
            else -> {
                super.onOptionsItemSelected(item)
            }
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int,  data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_ADD
            && resultCode == RESULT_OK
        ) {
            notesAdapter.refresh(loadNotesFromDatabase())
        }
    }

    private fun loadNotesFromDatabase(): List<Note> {
        val db: SQLiteDatabase = dbHelper.getWritableDatabase()
        val projection = arrayOf(
            BaseColumns._ID,
            NoteEntry.COLUMN_NAME_STATE,
            NoteEntry.COLUMN_NAME_DATE,
            NoteEntry.COLUMN_NAME_CONTENT,
            NoteEntry.COLUMN_NAME_PRIORITY
        )
        val c = db.query(
            NoteEntry.TABLE_NAME,
            projection,
            null,
            null,
            null,
            null,
            null
        )
        //  从数据库中查询数据，并转换成 JavaBeans
        val res: MutableList<Note> = ArrayList()
        while (c.moveToNext()) {
            val itemId = c.getLong(
                c.getColumnIndexOrThrow(BaseColumns._ID)
            )
            val itemDate = c.getString(
                c.getColumnIndexOrThrow(NoteEntry.COLUMN_NAME_DATE)
            )
            val itemState = c.getInt(
                c.getColumnIndexOrThrow(NoteEntry.COLUMN_NAME_STATE)
            )
            val itemContent = c.getString(
                c.getColumnIndexOrThrow(NoteEntry.COLUMN_NAME_CONTENT)
            )
            val itemPriority = c.getInt(
                c.getColumnIndexOrThrow(NoteEntry.COLUMN_NAME_PRIORITY)
            )
            res.add(Note(itemId, itemDate, itemState, itemContent, itemPriority))
        }
        c.close()
        return res
    }

    private fun
            deleteNote(note: Note): Boolean {
        val db: SQLiteDatabase = dbHelper.writableDatabase
        // Define 'where' part of query.
        val selection: String = BaseColumns._ID + " = ?"
        // Specify arguments in placeholder order.
        val selectionArgs = arrayOf("" + note.getId())
        // Issue SQL statement.
        val deletedRows = db.delete(NoteEntry.TABLE_NAME, selection, selectionArgs)
        Log.d("deleteNote", note.getContent())
        //  删除数据
        return deletedRows > 0
    }

    private fun updateNode(note: Note): Boolean {
        val db: SQLiteDatabase = dbHelper.writableDatabase

// New value for one column
        val values = ContentValues()
        values.put(NoteEntry.COLUMN_NAME_DATE, note.dateToString())
        values.put(NoteEntry.COLUMN_NAME_STATE, note.getState().intValue)
        values.put(NoteEntry.COLUMN_NAME_CONTENT, note.getContent())
        values.put(NoteEntry.COLUMN_NAME_PRIORITY, note.getPriority())

// Which row to update, based on the title
        val selection: String = BaseColumns._ID + " = ?"
        val selectionArgs = arrayOf("" + note.getId())
        val count = db.update(
            NoteEntry.TABLE_NAME,
            values,
            selection,
            selectionArgs
        )
        //  更新数据
        Log.d("updateNote", note.getContent())
        return count > 0
    }

}