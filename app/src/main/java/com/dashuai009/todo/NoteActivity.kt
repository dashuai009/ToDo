package com.dashuai009.todo;

import android.app.Activity
import android.app.DatePickerDialog.OnDateSetListener
import android.app.TimePickerDialog.OnTimeSetListener
import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.*
import android.widget.AdapterView.OnItemSelectedListener
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.DialogFragment
import com.dashuai009.todo.db.TodoDbHelper
import com.dashuai009.todo.ui.DatePickerFragment
import com.dashuai009.todo.ui.TimePickerFragment
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import com.dashuai009.todo.db.TodoContract.NoteEntry;


class NoteActivity : AppCompatActivity(), OnTimeSetListener, OnDateSetListener,
    OnItemSelectedListener {
    private lateinit var editText: EditText
    private lateinit var dateText: EditText
    private lateinit var timeText: EditText
    private lateinit var addBtn: Button
    private lateinit var prioritySpinner: Spinner
    private val dbHelper: TodoDbHelper = TodoDbHelper(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_note)
        val c = Calendar.getInstance()
        todoYear = c[Calendar.YEAR]
        todoMonth = c[Calendar.MONTH]
        todoDay = c[Calendar.DAY_OF_MONTH]
        todoHour = c[Calendar.HOUR_OF_DAY]
        todoMinute = c[Calendar.MINUTE]
        setTitle(R.string.take_a_note)
        editText = findViewById(R.id.edit_text)
        editText.setFocusable(true)
        editText.requestFocus()
        val inputManager = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        inputManager.showSoftInput(editText, 0)
        addBtn = findViewById(R.id.btn_add)
        addBtn.setOnClickListener(View.OnClickListener {
            val content: CharSequence = editText.getText()
            if (TextUtils.isEmpty(content)) {
                Toast.makeText(
                    this@NoteActivity,
                    "No content to add", Toast.LENGTH_SHORT
                ).show()
                return@OnClickListener
            }
            val succeed = saveNote2Database(content.toString().trim { it <= ' ' })
            if (succeed) {
                Toast.makeText(
                    this@NoteActivity,
                    "Note added", Toast.LENGTH_SHORT
                ).show()
                setResult(RESULT_OK)
            } else {
                Toast.makeText(
                    this@NoteActivity,
                    "Error", Toast.LENGTH_SHORT
                ).show()
            }
            finish()
        })
        dateText = findViewById(R.id.editTextDate)
        timeText = findViewById(R.id.editTextTime)

        prioritySpinner = findViewById(R.id.spinner)
        val adapter = ArrayAdapter.createFromResource(
            this,
            R.array.priority_spinner, android.R.layout.simple_spinner_item
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        prioritySpinner.adapter = adapter
        prioritySpinner.onItemSelectedListener = this as OnItemSelectedListener
    }



    override fun onItemSelected(
        parent: AdapterView<*>, view: View,
        pos: Int, id: Long
    ) {
        // An item was selected. You can retrieve the selected item using
        // parent.getItemAtPosition(pos)
        priorityValue = pos
        Log.d("priorityValue", "${priorityValue}" + parent.getItemAtPosition(pos))
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {
        // Another interface callback
    }

    fun showTimePickerDialog(v: View?) {
        val newFragment: DialogFragment = TimePickerFragment()
        newFragment.show(supportFragmentManager, "timePicker")
    }

    fun showDatePickerDialog(v: View?) {
        val newFragment: DialogFragment = DatePickerFragment()
        newFragment.show(supportFragmentManager, "datePicker")
    }

    override fun onDateSet(view: DatePicker, year: Int, month: Int, day: Int) {
        // Do something with the date chosen by the user
        todoYear = year
        todoMonth = month
        todoDay = day
        dateText.setText("$year-$month-$day")
    }

    override fun onTimeSet(view: TimePicker, hourOfDay: Int, minute: Int) {
        // Do something with the time chosen by the user
        todoHour = hourOfDay
        todoMinute = minute
        timeText.setText("$hourOfDay:$minute")
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    private val stringFrom5int: String
        get() {
            val ft = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.ENGLISH)
            var tag: Date? = null
            try {
                tag =
                    ft.parse("" + todoYear + "-" + todoMonth + "-" + todoDay + " " + todoHour + ":" + todoMinute)
                Log.d("todoTTT", tag.toString())
            } catch (e: ParseException) {
                Log.d("parse", "Unparseable using $ft")
            }
            val ft2 = SimpleDateFormat("EEE, d MMM yyyy HH:mm", Locale.ENGLISH)
            Log.d("todoTTT", ft2.format(tag))
            return ft2.format(tag)
        }

    private fun saveNote2Database(content: String): Boolean {
        val db: SQLiteDatabase = dbHelper.writableDatabase
        // Create a new map of values, where column names are the keys
        val values = ContentValues()
        values.put(NoteEntry.COLUMN_NAME_DATE, stringFrom5int)
        values.put(NoteEntry.COLUMN_NAME_STATE, 0)
        values.put(NoteEntry.COLUMN_NAME_CONTENT, content)
        values.put(NoteEntry.COLUMN_NAME_PRIORITY, priorityValue)
        Log.d("todoTTT", values.toString())

// Insert the new row, returning the primary key value of the new row
        val newRowId = db.insert(NoteEntry.TABLE_NAME, null, values)
        db.close()
        //  插入一条新数据，返回是否插入成功
        return newRowId != -1L
    }

    companion object {
        private var todoYear = 0
        private var todoMonth = 0
        private var todoDay = 0
        private var todoHour = 0
        private var todoMinute = 0
        private var priorityValue = 0
    }
}