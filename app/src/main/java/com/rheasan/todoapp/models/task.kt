package com.rheasan.todoapp.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date
import java.util.UUID

@Entity
data class Task(
    @PrimaryKey val id: UUID,
    @ColumnInfo("task_title") var taskTitle: String,
    @ColumnInfo("created_at") val createdAt: Date,
    @ColumnInfo("updated_at") var updatedAt: Date,
    @ColumnInfo("deletedAt") var deletedAt: Date? = null,
){
    constructor() : this(UUID.randomUUID(), "", Date(), Date(), null)
    fun convertToLongs(): FirebaseSafeTask {
        return FirebaseSafeTask(
            id.toString(),
            taskTitle,
            updatedAt = updatedAt.time,
            createdAt = createdAt.time,
            deletedAt = deletedAt?.time
        )
    }
}

// all datatypes saved in firebase need a no argument constructor to deserialize so store id as
// string instead of UUID. Dates also need to be converted to Longs
data class FirebaseSafeTask(
    val id: String,
    var taskTitle: String,
    var createdAt: Long,
    var updatedAt: Long,
    var deletedAt: Long? = null
) {
    constructor() : this(UUID.randomUUID().toString(), "", Date().time, Date().time, null)
    fun convertToDates(): Task {
        return Task(
            UUID.fromString(id),
            taskTitle,
            createdAt = Date(createdAt),
            updatedAt = Date(createdAt),
            deletedAt = deletedAt?.let { Date(it) }
        )
    }
}