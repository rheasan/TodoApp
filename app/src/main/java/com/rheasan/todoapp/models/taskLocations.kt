package com.rheasan.todoapp.models

import java.util.UUID

enum class TaskLocations {
    Firebase,
    LocalRoomDB
}
interface ReadLocation {
    var name: TaskLocations
    suspend fun getTask(id: UUID) : Task
    suspend fun getAllTasks() : List<Task>
}
interface WriteLocation {
    var name: TaskLocations
    suspend fun addTask(task: Task)
    suspend fun updateTitle(id: UUID, newTitle: String)
    suspend fun deleteTask(id: UUID)
}