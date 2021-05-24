package com.dashuai009.todo.util

import java.io.*

object FileUtils {
    /**
     * 使用BufferedWriter进行添加文本内容
     */
    fun writeContentToFile(file: File?, content: String?) {
        var out: BufferedWriter? = null
        try {
            //FileOutputStream(file, true),第二个参数为true是追加内容，false是覆盖
            out = BufferedWriter(OutputStreamWriter(FileOutputStream(file, false)))
            out.newLine() //换行
            out.write(content)
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            closeQuietly(out)
        }
    }

    /**
     * 使用BufferedReader 进行文件内容的读取
     */
    fun readContentFromFile(file: File?): List<String> {
        val contents: MutableList<String> = ArrayList()
        var reader: BufferedReader? = null
        if (file != null && file.isFile && file.exists()) { // 判断文件是否存在
            try {
                reader = BufferedReader(
                    InputStreamReader(
                        FileInputStream(file), "UTF-8"
                    )
                )
                var line: String
                while (reader.readLine().also { line = it } != null) {
                    contents.add(line)
                }
            } catch (e: IOException) {
                e.printStackTrace()
            } finally {
                closeQuietly(reader)
            }
        }
        return contents
    }

    fun closeQuietly(c: Closeable?) {
        if (c != null) {
            try {
                c.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }
}
