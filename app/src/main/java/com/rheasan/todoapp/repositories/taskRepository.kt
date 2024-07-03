package com.rheasan.todoapp.repositories

import com.rheasan.todoapp.models.SaveLocation
import com.rheasan.todoapp.models.SaveLocationType
import com.rheasan.todoapp.models.Task
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Date
import java.util.UUID

interface TaskRepository {
    fun generateMockData()
    fun getAllTasks(locationType: SaveLocationType?) : List<Task>
    fun deleteTask(id: UUID)
    fun updateTaskTitle(id: UUID, newTitle: String)
    fun addTask(task: Task)
    fun addSaveLocation(saveLocation: SaveLocation)
}



class TaskRepositoryClass : TaskRepository {
    private var tasks: MutableList<Task> = mutableListOf()
    private var saveLocations: MutableList<SaveLocation> = mutableListOf()

    override fun generateMockData() {
        for(i in 0..10) {
            addTask(Task(
                id = UUID.randomUUID(),
                taskTitle = "task$i",
                createdAt = Date(),
                updatedAt = Date(),
                deletedAt = null
            ))
        }

    }
    override fun addSaveLocation(saveLocation: SaveLocation) {
        saveLocations.add(saveLocation)
    }
    override fun getAllTasks(locationType: SaveLocationType?) : List<Task> {
        if(saveLocations.isEmpty()) {
            return tasks.filter {it.deletedAt == null}
        }
        for(location in saveLocations) {
            if (location.name == locationType) {
                return location.getAllTasks()
            }
        }
        return listOf()
    }
    override fun deleteTask(id: UUID) {
        for(task in tasks) {
            if(task.id == id) {
                task.deletedAt = Date()
                break
            }
        }
        for(location in saveLocations) {
            location.deleteTask(id)
        }
    }

    override fun updateTaskTitle(id: UUID, newTitle: String) {
        for(task in tasks) {
            if(task.id == id) {
                task.taskTitle = newTitle
                task.updatedAt = Date()
                break
            }
        }
        CoroutineScope(Dispatchers.IO).launch {
            for(location in saveLocations) {
                location.updateTitle(id, newTitle)
            }
        }
    }

    override fun addTask(task: Task) {
        tasks.add(task)
        CoroutineScope(Dispatchers.IO).launch {
            for(location in saveLocations) {
                location.addTask(task)
            }
        }
    }
}
object TaskRepositoryInstance {
    private var instance : TaskRepository? = null
    private fun createInstance() {
        instance = TaskRepositoryClass()
    }
    fun getInstance(): TaskRepository {
        if(instance == null) {
            createInstance()
        }
        return instance!!
    }
}