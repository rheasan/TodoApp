package com.rheasan.todoapp.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rheasan.todoapp.models.Task
import com.rheasan.todoapp.repositories.TaskRepositoryInstance
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import java.util.UUID

class TaskAdapterViewModel : ViewModel() {
    private var repository = TaskRepositoryInstance.getInstance()

    fun updateTitle(id: UUID, newTitle: String) {
        CoroutineScope(Dispatchers.IO).launch {
            repository.updateTaskTitle(id, newTitle)
        }
    }

    fun deleteTask(id: UUID) {
        CoroutineScope(Dispatchers.IO).launch {
            repository.deleteTask(id)
        }
    }

    fun getAllTasks(): MutableList<Task> {
        var res: MutableList<Task>
        runBlocking {
            res = repository.getAllTasks().toMutableList()
        }
        return res
    }
}