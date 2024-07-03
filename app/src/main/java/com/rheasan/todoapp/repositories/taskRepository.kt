package com.rheasan.todoapp.repositories

import com.rheasan.todoapp.models.ReadLocation
import com.rheasan.todoapp.models.TaskLocations
import com.rheasan.todoapp.models.Task
import com.rheasan.todoapp.models.WorkManagerHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import java.util.Date
import java.util.UUID

interface TaskRepository {
    fun generateMockData()
    fun getAllTasks() : List<Task>
    fun getRepoTasks() : List<Task>
    fun deleteTask(id: UUID)
    fun updateTaskTitle(id: UUID, newTitle: String)
    fun addTask(task: Task)
    fun addReadLocation(readLocation: ReadLocation)
    fun setDefaultReadLocation(taskLocations: TaskLocations)
}



class TaskRepositoryClass : TaskRepository {
    private val tasks: MutableList<Task> = mutableListOf()
    private val readLocations: MutableMap<TaskLocations, ReadLocation> = mutableMapOf()
    private var defaultReadLocation : TaskLocations? = null

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
    override fun addReadLocation(readLocation: ReadLocation) {
        readLocations[readLocation.name] =  readLocation
    }

    override fun setDefaultReadLocation(taskLocations: TaskLocations) {
        defaultReadLocation = taskLocations
    }

    override fun getAllTasks() : List<Task> {
        if(defaultReadLocation == null){
            return listOf()
        }
        return runBlocking {
            withContext(Dispatchers.IO){
                val readLocation = readLocations[defaultReadLocation]!!
                readLocation.getAllTasks()
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
        WorkManagerHelper.enqueueDeleteTask(id)
    }

    override fun updateTaskTitle(id: UUID, newTitle: String) {
        for(task in tasks) {
            if(task.id == id) {
                task.taskTitle = newTitle
                task.updatedAt = Date()
                break
            }
        }
        WorkManagerHelper.enqueueUpdateTitleTask(id, newTitle)
    }

    override fun addTask(task: Task) {
        tasks.add(task)
        WorkManagerHelper.enqueueAddTask(task)
    }

    override fun getRepoTasks() : List<Task> {
        return tasks
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