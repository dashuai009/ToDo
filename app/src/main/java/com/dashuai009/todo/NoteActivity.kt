package com.dashuai009.todo;


import android.app.DatePickerDialog.OnDateSetListener
import android.app.TimePickerDialog.OnTimeSetListener
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.*
import android.widget.AdapterView.OnItemSelectedListener
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.DialogFragment
import com.dashuai009.todo.db.entity.Note
import com.dashuai009.todo.ui.DatePickerFragment
import com.dashuai009.todo.ui.TimePickerFragment
import kotlinx.coroutines.*
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*


class NoteActivity : AppCompatActivity(), OnTimeSetListener, OnDateSetListener,
    OnItemSelectedListener {
    private lateinit var editText: EditText
    private lateinit var dateText: TextView
    private lateinit var timeText: TextView
    private lateinit var addBtn: Button
    private lateinit var prioritySpinner: Spinner
    private lateinit var curNote: Note;


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_note)

        curNote = intent.extras!!["note"] as Note
        Log.d("tagtagtag",curNote.toString())

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
            MainScope().launch(Dispatchers.IO) {
                saveNote2Database(content.toString().trim { it <= ' ' })
            }
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
        prioritySpinner.setSelection(curNote.priority)
        prioritySpinner.onItemSelectedListener = this as OnItemSelectedListener

        setCurNote(curNote)
    }

    fun setCurNote(x: Note) {
        curNote = x;
        editText.setText(curNote.content)
        val ftyMd = SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH)
        val ftHm = SimpleDateFormat("HH:mm", Locale.ENGLISH)
        dateText.setText(ftyMd.format(curNote.date))
        timeText.setText(ftHm.format(curNote.date))
        val c = Calendar.getInstance()
        c.time = curNote.date
        todoYear = c[Calendar.YEAR]
        todoMonth = c[Calendar.MONTH] + 1
        todoDay = c[Calendar.DAY_OF_MONTH]
        todoHour = c[Calendar.HOUR_OF_DAY]
        todoMinute = c[Calendar.MINUTE]
        // prioritySpinner.setSelection(priorityValue)
    }


    override fun onItemSelected(parent: AdapterView<*>, view: View, pos: Int, id: Long) {
        curNote.priority = pos
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
        todoMonth = month + 1
        todoDay = day
        dateText.setText("$year-${month + 1}-$day")
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
        return tag!!
    }

    private suspend fun saveNote2Database(content: String) {
        //Note: var content: String,
        //      var date: Date,
        //      var Done: Boolean,
        //      var priority: Int = 2
        //          id:Long=0
        curNote.date = stringFrom5int()
        curNote.content = content

        intent = Intent()
        intent.putExtra("curNode",curNote)

        setResult(RESULT_OK, intent)
        finish()
    }

    companion object {
        private var todoYear = 0
        private var todoMonth = 0
        private var todoDay = 0
        private var todoHour = 0
        private var todoMinute = 0
    }
}