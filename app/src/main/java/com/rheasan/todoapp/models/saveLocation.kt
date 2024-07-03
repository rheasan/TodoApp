package com.rheasan.todoapp.models

import java.util.UUID

enum class SaveLocationType {
    firebase,
    localRoomDb
}
interface SaveLocation {
    var name: SaveLocationType
    fun addTask(task: Task)
    fun updateTitle(id: UUID, newTitle: String)
    fun getTask(id: UUID) : Task
    fun getAllTasks() : List<Task>
    fun deleteTask(id: UUID)
}