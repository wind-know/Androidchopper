package com.example.androidchopper.utils.importer
import android.content.Context
import android.util.Log
import com.example.androidchopper.data.database.AppDatabase
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.example.androidchopper.data.model.Question
import com.example.androidchopper.data.model.toEntity
import com.example.androidchopper.data.repository.QuestionRepository
import com.google.gson.JsonSyntaxException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.IOException
import java.lang.reflect.Type

object QuestionImporter {
    // 修改 QuestionImporter 的返回类型为非空布尔值
    @Throws(IOException::class, JsonSyntaxException::class)
    suspend fun importFromAssets(context: Context): Boolean {
        return withContext(Dispatchers.IO) {
            val jsonString =
                context.assets.open("questions.json").bufferedReader().use { it.readText() }
                    .takeIf { it.isNotBlank() } ?: return@withContext false

            val type = object : TypeToken<List<Question>>() {}.type
            val questions =
                Gson().fromJson<List<Question>>(jsonString, type) ?: return@withContext false
            if (questions.isNotEmpty()) {
                questions.forEach { question ->
                    Log.v("QuestionImporter", "Parsed: ${question.id} - ${question.content.take(20)}...")
                }
            } else {
                Log.w("QuestionImporter", "Empty questions list")
            }
            val entities = questions.mapNotNull { it.toEntity() }
            if (entities.isEmpty()) return@withContext false
            val insertedIds = AppDatabase.getInstance(context)
                .questionDao()
                .insertAll(entities)
            // 打印插入的记录数和第一个ID
            Log.d("DataImport", "Inserted ${insertedIds.size} records. First ID: ${insertedIds.firstOrNull()}")
            true
        }
    }
}
