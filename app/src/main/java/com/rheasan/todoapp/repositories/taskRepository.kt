package com.rheasan.todoapp.repositories

import com.rheasan.todoapp.models.SaveLocation
import com.rheasan.todoapp.models.SaveLocationType
import com.rheasan.todoapp.models.Task
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import java.util.Date
import java.util.UUID

interface TaskRepository {
    fun generateMockData()
    fun getAllTasks() : List<Task>
    fun deleteTask(id: UUID)
    fun updateTaskTitle(id: UUID, newTitle: String)
    fun addTask(task: Task)
    fun addSaveLocation(saveLocation: SaveLocation)
    fun setDefaultReadLocation(saveLocationType: SaveLocationType)
}



class TaskRepositoryClass : TaskRepository {
    private val tasks: MutableList<Task> = mutableListOf()
    private val saveLocations: MutableMap<SaveLocationType, SaveLocation> = mutableMapOf()
    private var defaultReadLocation : SaveLocationType? = null

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
        saveLocations[saveLocation.name] =  saveLocation
    }

    override fun setDefaultReadLocation(saveLocationType: SaveLocationType) {
        defaultReadLocation = saveLocationType
    }

    override fun getAllTasks() : List<Task> {
        if(defaultReadLocation == null){
            return listOf()
        }
        return runBlocking {
            withContext(Dispatchers.IO){
                val saveLocation = saveLocations[defaultReadLocation]!!
                saveLocation.getAllTasks()
            }
        }
    }
    override fun deleteTask(id: UUID) {
        for(task in tasks) {
            if(task.id == id) {
                task.deletedAt = Date()
                break
            }
        }
        CoroutineScope(Dispatchers.IO).launch {
            for((_, location) in saveLocations) {
                location.deleteTask(id)
            }
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
            for((_, location) in saveLocations) {
                location.updateTitle(id, newTitle)
            }
        }
    }

    override fun addTask(task: Task) {
        tasks.add(task)
        CoroutineScope(Dispatchers.IO).launch {
            for((_, location) in saveLocations) {
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