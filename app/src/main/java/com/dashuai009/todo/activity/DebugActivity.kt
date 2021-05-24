package com.dashuai009.todo.activity

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Environment
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.NonNull
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.dashuai009.todo.R
import com.dashuai009.todo.util.FileUtils
import java.io.File
import java.io.IOException

class DebugActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_debug)
        setTitle(R.string.action_debug)
        val printBtn: Button = findViewById(R.id.btn_print_path)
        val pathText: TextView = findViewById(R.id.text_path)
        printBtn.setOnClickListener {
            val sb = StringBuilder()
            sb.append("===== Internal Private =====\n").append(internalPath)
                .append("===== External Private =====\n").append(externalPrivatePath)
                .append("===== External Public =====\n").append(externalPublicPath)
            pathText.text = sb
        }
        val permissionBtn: Button = findViewById(R.id.btn_request_permission)
        permissionBtn.setOnClickListener(View.OnClickListener {
            val state: Int = ActivityCompat.checkSelfPermission(
                this@DebugActivity,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
            if (state == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(
                    this@DebugActivity, "already granted",
                    Toast.LENGTH_SHORT
                ).show()
                return@OnClickListener
            }
            ActivityCompat.requestPermissions(
                this@DebugActivity, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                REQUEST_CODE_STORAGE_PERMISSION
            )
        })
        val fileWriteBtn: Button = findViewById(R.id.btn_write_files)
        val fileText: TextView = findViewById(R.id.text_files)
        fileWriteBtn.setOnClickListener { // TODO 把一段文本写入某个存储区的文件中，再读出来，显示在 fileText 上
            Thread {
                // Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                val file = File(baseContext.externalCacheDir, "test")
                FileUtils.writeContentToFile(file, "#title \ntest content.")
                val contents: List<String> = FileUtils.readContentFromFile(file)
                runOnUiThread {
                    fileText.text = ""
                    for (content in contents) {
                        fileText.append(content.trimIndent())
                    }
                }
            }.start()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, @NonNull permissions: Array<String?>,
        @NonNull grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (permissions.size == 0 || grantResults.size == 0) {
            return
        }
        if (requestCode == REQUEST_CODE_STORAGE_PERMISSION) {
            val state = grantResults[0]
            if (state == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(
                    this@DebugActivity, "permission granted",
                    Toast.LENGTH_SHORT
                ).show()
            } else if (state == PackageManager.PERMISSION_DENIED) {
                Toast.makeText(
                    this@DebugActivity, "permission denied",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    //        Context context = this;
//        context.getCacheDir();
//        context.getFilesDir();
    private val internalPath: String
        private get() {
            //        Context context = this;
            //        context.getCacheDir();
            //        context.getFilesDir();
            val dirMap: MutableMap<String, File?> = LinkedHashMap()
            dirMap["cacheDir"] = cacheDir
            dirMap["filesDir"] = filesDir
            dirMap["customDir"] = getDir("custom", MODE_PRIVATE)
            return getCanonicalPath(dirMap)
        }

    private val externalPrivatePath: String
        private get() {
            val dirMap: MutableMap<String, File?> = LinkedHashMap()
            dirMap["cacheDir"] = externalCacheDir
            dirMap["filesDir"] = getExternalFilesDir(null)
            dirMap["picturesDir"] = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
            return getCanonicalPath(dirMap)
        }
    private val externalPublicPath: String
        private get() {
            val dirMap: MutableMap<String, File?> = LinkedHashMap()
            dirMap["rootDir"] = Environment.getExternalStorageDirectory()
            dirMap["picturesDir"] =
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
            return getCanonicalPath(dirMap)
        }

    companion object {
        private const val REQUEST_CODE_STORAGE_PERMISSION = 1001
        private fun getCanonicalPath(dirMap: Map<String, File?>): String {
            val sb = StringBuilder()
            try {
                for (name in dirMap.keys) {
                    sb.append(name)
                        .append(": ")
                        .append(dirMap[name]!!.canonicalPath)
                        .append('\n')
                }
            } catch (e: IOException) {
                e.printStackTrace()
            }
            return sb.toString()
        }
    }
}
