package com.example.data

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "tasks")
data class TaskEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val description: String,
    val dueDate: Long = System.currentTimeMillis(),
    val isCompleted: Boolean = false,
    val tags: String = "", // comma-separated strings
    val priority: String = "Medium", // High, Medium, Low
    val assignedTeamMember: String = "", // Assigned team member name if collaborative
    val isCollaborative: Boolean = false,
    val sharedCode: String = "", // Team sharing room code
    val encrypted: Boolean = false, // True if fields are encrypted with user key
    val deviceSyncId: String = "local-device",
    val lastUpdated: Long = System.currentTimeMillis()
)

@Entity(
    tableName = "subtasks",
    foreignKeys = [
        ForeignKey(
            entity = TaskEntity::class,
            parentColumns = ["id"],
            childColumns = ["taskId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["taskId"])]
)
data class SubTaskEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val taskId: Int,
    val title: String,
    val isCompleted: Boolean = false
)
