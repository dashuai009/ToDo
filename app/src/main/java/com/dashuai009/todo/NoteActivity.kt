package com.dashuai009.todo;


import android.app.DatePickerDialog.OnDateSetListener
import android.app.TimePickerDialog.OnTimeSetListener
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.*
import android.widget.AdapterView.OnItemSelectedListener
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.DialogFragment
import androidx.room.Room
import com.dashuai009.todo.db.TodoDataBase
import com.dashuai009.todo.db.dao.NoteDao
import com.dashuai009.todo.db.entity.Note
import com.dashuai009.todo.ui.DatePickerFragment
import com.dashuai009.todo.ui.TimePickerFragment
import kotlinx.coroutines.*
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*


class NoteActivity : AppCompatActivity(), OnTimeSetListener, OnDateSetListener,
    OnItemSelectedListener , CoroutineScope by MainScope() {
    private lateinit var editText: EditText
    private lateinit var dateText: EditText
    private lateinit var timeText: EditText
    private lateinit var addBtn: Button
    private lateinit var prioritySpinner: Spinner
    private lateinit var dao:NoteDao


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_note)

        val db = Room.databaseBuilder(
            applicationContext,
            TodoDataBase::class.java, "todo"
        ).build()
        dao = db.noteDao();


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
            launch {
                val succeed = async {saveNote2Database(content.toString().trim { it <= ' ' })}
                if (succeed.await()) {
                    /*
                    TODO  未解决的问题，无法在协程中v操作view
                     android.view.ViewRootImpl$CalledFromWrongThreadException: Only the original thread that created a view hierarchy can touch its views.
                     */

                    /*Toast.makeText(
                        this@NoteActivity,
                        "Note added", Toast.LENGTH_SHORT
                    ).show()*/
                    setResult(RESULT_OK)
                    finish()
                } else {
                    /*Toast.makeText(
                        this@NoteActivity,
                        "Error", Toast.LENGTH_SHORT
                    ).show()*/
                }
                //setResult(RESULT_OK)
                //finish()
            }
        })
        var thread = object : Thread() {
            override fun run() {
                finish();
            }
        }
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

    fun stringFrom5int(): Date {
        val ft = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.ENGLISH)
        var tag: Date? = null
        try {
            tag = ft.parse("$todoYear-$todoMonth-$todoDay $todoHour:$todoMinute")
            Log.d("todoTTT", tag.toString())
        } catch (e: ParseException) {
            Log.d("parse", "Unparseable using $ft")
        }
        //val ft2 = SimpleDateFormat("EEE, d MMM yyyy HH:mm", Locale.ENGLISH)
        //Log.d("todoTTT", ft2.format(tag))
        return tag!!
    }

    private suspend fun saveNote2Database(content: String): Boolean {

        val newRowId=dao.insert(Note(content,stringFrom5int(),false,priorityValue))
        return newRowId != 0L
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