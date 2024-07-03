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
)
