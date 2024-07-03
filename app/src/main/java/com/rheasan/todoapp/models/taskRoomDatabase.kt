package com.rheasan.todoapp.models

import android.content.Context
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import java.util.Date
import java.util.UUID

class Converters {
    @TypeConverter
    fun fromTimestamp(value: Long?): Date? {
        return value?.let { Date(it) }
    }

    @TypeConverter
    fun dateToTimestamp(date: Date?): Long? {
        return date?.time
    }
}

@Database(entities = [Task::class], version = 1)
@TypeConverters(Converters::class)
abstract class TaskDB : RoomDatabase() {
    abstract fun taskDao() : TaskDao
    companion object {
        private var INSTANCE: TaskDB? = null

        private val lock = Any()

        fun getInstance(context: Context): TaskDB {
            synchronized(lock) {
                if (INSTANCE == null) {
                    INSTANCE = Room
                        .databaseBuilder(context.applicationContext, TaskDB::class.java, "tasks")
                        .build()
                }
                return INSTANCE!!
            }
        }
    }
}

@Dao
interface TaskDao {
    @Insert
    fun insertNewTask(task: Task)

    @Query("SELECT * from task")
    fun getAllTasks() : List<Task>

    @Query("SELECT * from task where id = :taskId")
    fun getTask(taskId: UUID) : Task

    @Query("UPDATE task SET `task_title` = :newTitle, `updated_at` = unixepoch('now') where id = :taskId")
    fun updateTaskTitle(taskId: UUID, newTitle: String)

    @Query("UPDATE task SET `deletedAt` = unixepoch('now') where id = :taskId")
    fun taskSetDeleted(taskId: UUID)
}
//object RoomDB {
//    private var instance: TaskDB? = null
//    fun getInstance(context: Context): TaskDB {
//        if(instance == null){
//            createInstance(context)
//        }
//        return instance!!
//    }
//    private fun createInstance(context: Context){
//        instance = Room.databaseBuilder(
//            context.applicationContext,
//            TaskDB::class.java,
//            "tasks"
//        ).build()
//    }
//}

class RoomDBProxy(context: Context) : SaveLocation {
    override var name = SaveLocationType.localRoomDb
    private var db : TaskDB = TaskDB.getInstance(context)

    override fun addTask(task: Task) {
        CoroutineScope(Dispatchers.IO).launch {
            db.taskDao().insertNewTask(task)
        }
    }

    override fun updateTitle(id: UUID, newTitle: String) {
        CoroutineScope(Dispatchers.IO).launch {
            db.taskDao().updateTaskTitle(id, newTitle)
        }
    }

    override suspend fun getTask(id: UUID): Task {
        return db.taskDao().getTask(id)
    }

    override suspend fun getAllTasks(): List<Task> {
        return db.taskDao().getAllTasks()
    }

    override fun deleteTask(id: UUID) {
        CoroutineScope(Dispatchers.IO).launch {
            db.taskDao().taskSetDeleted(id)
        }
    }
}