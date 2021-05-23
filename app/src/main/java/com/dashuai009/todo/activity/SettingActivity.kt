package com.dashuai009.todo.activity

import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.widget.Switch
import androidx.appcompat.app.AppCompatActivity
import com.dashuai009.todo.R

class SettingActivity() : AppCompatActivity() {

    private val KEY_IS_NEED_SORT: String = "is_need_sort";
    lateinit private var mSharedPreferences: SharedPreferences


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setting)


        mSharedPreferences = baseContext.getSharedPreferences("todo", MODE_PRIVATE);

        val commentSwitch: Switch = findViewById(R.id.switch_comment)
        commentSwitch.setOnCheckedChangeListener { _, isChecked ->
            val editor = mSharedPreferences.edit()
            editor.putBoolean(KEY_IS_NEED_SORT, isChecked)
            editor.apply()
        }

        val isOpen = mSharedPreferences.getBoolean(KEY_IS_NEED_SORT, false)
        commentSwitch.setChecked(isOpen)
        Log.d(KEY_IS_NEED_SORT,"wwwww");
    }
}