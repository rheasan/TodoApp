package com.rheasan.todoapp.models

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.Data
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.rheasan.todoapp.repositories.TaskRepositoryInstance
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import java.util.Date
import java.util.UUID

object WorkManagerHelper {
    private val writeLocations: MutableMap<TaskLocations, WriteLocation> = mutableMapOf()
    private lateinit var workManager : WorkManager
    fun addWriteLocations(writeLocation: WriteLocation) {
        writeLocations[writeLocation.name] =  writeLocation
    }
    fun getWriteLocations() : Map<TaskLocations, WriteLocation> {
        return writeLocations
    }
    fun setupWorkers(context: Context) {
        workManager =  WorkManager.getInstance(context)
    }

    fun enqueueDeleteTask(id: UUID) {
        val taskData = Data.Builder()
            .putString("id", id.toString())
            .build()
        val workRequest = OneTimeWorkRequestBuilder<DeleteTaskWorker>()
            .setInputData(taskData)
            .build()

        workManager.enqueue(workRequest)
    }
    fun enqueueUpdateTitleTask(id: UUID, newTitle: String) {
        val taskData = Data.Builder()
            .putString("id", id.toString())
            .putString("newTitle", newTitle)
            .build()

        val workRequest = OneTimeWorkRequestBuilder<UpdateTitleWorker>()
            .setInputData(taskData)
            .build()

        workManager.enqueue(workRequest)
    }
    fun enqueueAddTask(task: Task) {
        val taskData = Data.Builder()
            .putString("id", task.id.toString())
            .putString("taskTitle", task.taskTitle)
            .putLong("createdAt", task.createdAt.time)
            .putLong("updatedAt", task.updatedAt.time)
            .build()
        val workRequest = OneTimeWorkRequestBuilder<AddTaskWorker>()
            .setInputData(taskData)
            .build()
        workManager.enqueue(workRequest)
    }
}

class DeleteTaskWorker(appContext: Context, workerParameters: WorkerParameters): CoroutineWorker(appContext, workerParameters) {
    override suspend fun doWork(): Result {
        val taskId = UUID.fromString(inputData.getString("id")!!)
        val writeLocations = WorkManagerHelper.getWriteLocations()
        withContext(Dispatchers.IO) {
            for((_, location) in writeLocations) {
                location.deleteTask(taskId)
            }
        }
        return Result.success()
    }
}

class UpdateTitleWorker(appContext: Context, workerParameters: WorkerParameters): CoroutineWorker(appContext, workerParameters) {
    override suspend fun doWork(): Result {
        val id = UUID.fromString(inputData.getString("id")!!)
        val newTitle = inputData.getString("newTitle")!!


        val writeLocations = WorkManagerHelper.getWriteLocations()
        withContext(Dispatchers.IO) {
            for ((_, location) in writeLocations) {
                location.updateTitle(id, newTitle)
            }
        }
        return Result.success()
    }
}

class AddTaskWorker(appContext: Context, workerParameters: WorkerParameters): CoroutineWorker(appContext, workerParameters) {
    override suspend fun doWork(): Result {
        val id = UUID.fromString(inputData.getString("id")!!)
        val taskTitle = inputData.getString("taskTitle")!!
        val createdAt = Date(inputData.getLong("createdAt", 0))
        val updatedAt = Date(inputData.getLong("updatedAt", 0))

        val task = Task(id, taskTitle, createdAt, updatedAt, null)

        val writeLocations = WorkManagerHelper.getWriteLocations()

        withContext(Dispatchers.IO) {
            for((_, location) in writeLocations) {
                location.addTask(task)
            }
        }
        return Result.success()
    }
}