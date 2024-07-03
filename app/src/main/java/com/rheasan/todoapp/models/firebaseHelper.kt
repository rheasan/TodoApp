package com.rheasan.todoapp.models

import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ServerValue
import kotlinx.coroutines.tasks.await
import java.util.UUID

class FirebaseHelper : ReadLocation, WriteLocation {
    override var name: TaskLocations = TaskLocations.Firebase
    private val database = FirebaseDatabase.getInstance().reference.child("tasks")

    override suspend fun addTask(task: Task) {
        val taskId = task.id.toString()
        database.child(taskId).setValue(task.convertToLongs())
    }

    override suspend fun updateTitle(id: UUID, newTitle: String) {
        val taskId = id.toString()
        database.child(taskId).child("taskTitle").setValue(newTitle)
        database.child(taskId).child("updatedAt").setValue(ServerValue.TIMESTAMP)
    }

    override suspend fun getTask(id: UUID): Task {
        val taskId = id.toString()
        val taskRef = database.child(taskId)
        val dataSnapshot = taskRef.get().await()
        val taskWithLongs = dataSnapshot.getValue(FirebaseSafeTask::class.java) ?: FirebaseSafeTask()
        return taskWithLongs.convertToDates()
    }

    override suspend fun getAllTasks(): List<Task> {
        val tasks = mutableListOf<FirebaseSafeTask>()
        val dataSnapshot = database.get().await()
        for (taskSnapshot in dataSnapshot.children) {
            val task = taskSnapshot.getValue(FirebaseSafeTask::class.java)
            task?.let { tasks.add(it) }
        }
        return tasks.filter { it.deletedAt == null }.map { it.convertToDates() }
    }

    override suspend fun deleteTask(id: UUID) {
        val taskId = id.toString()
        database.child(taskId).child("deletedAt").setValue(ServerValue.TIMESTAMP)
    }
}