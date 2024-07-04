package com.rheasan.todoapp.viewModels

import android.content.Context
import androidx.lifecycle.ViewModel
import com.google.firebase.FirebaseApp
import com.rheasan.todoapp.models.db.FirebaseHelper
import com.rheasan.todoapp.models.db.RoomDBProxy
import com.rheasan.todoapp.models.Task
import com.rheasan.todoapp.models.TaskLocations
import com.rheasan.todoapp.models.WorkManagerHelper
import com.rheasan.todoapp.models.network.getDevName
import com.rheasan.todoapp.repositories.TaskRepository
import com.rheasan.todoapp.repositories.TaskRepositoryInstance
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Date
import java.util.UUID

class MainActivityViewModel(context: Context) : ViewModel() {
    private var repository: TaskRepository

    init {
        // initialize firebase
        FirebaseApp.initializeApp(context)

        // setup task repository
        repository = TaskRepositoryInstance.getInstance()
        repository.setDefaultReadLocation(TaskLocations.Firebase)
        repository.addReadLocation(RoomDBProxy(context))
        repository.addReadLocation(FirebaseHelper())

        // setup workmanager
        WorkManagerHelper.setupWorkers(context)
        WorkManagerHelper.addWriteLocations(FirebaseHelper())
        WorkManagerHelper.addWriteLocations(RoomDBProxy(context))
    }

    fun createTask(taskTitle: String) : Task? {
        if (taskTitle.isNotEmpty()) {
            val newTask = Task(
                id = UUID.randomUUID(),
                createdAt = Date(),
                updatedAt = Date(),
                deletedAt = null,
                taskTitle = taskTitle
            )
            CoroutineScope(Dispatchers.IO).launch {
                repository.addTask(newTask)
            }
            return newTask
        }
        return null
    }
    suspend fun devName(): String {
        return getDevName() ?: "Offline Dev"
    }
}