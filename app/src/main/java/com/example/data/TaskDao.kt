package com.example.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskDao {
    @Query("SELECT * FROM tasks ORDER BY dueDate ASC")
    fun getAllTasks(): Flow<List<TaskEntity>>

    @Query("SELECT * FROM tasks WHERE id = :taskId")
    suspend fun getTaskById(taskId: Int): TaskEntity?

    @Query("SELECT * FROM subtasks WHERE taskId = :taskId")
    fun getSubTasksForTask(taskId: Int): Flow<List<SubTaskEntity>>

    @Query("SELECT * FROM subtasks WHERE taskId = :taskId")
    suspend fun getSubTasksForTaskSync(taskId: Int): List<SubTaskEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTask(task: TaskEntity): Long

    @Update
    suspend fun updateTask(task: TaskEntity)

    @Delete
    suspend fun deleteTask(task: TaskEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSubTask(subTask: SubTaskEntity): Long

    @Update
    suspend fun updateSubTask(subTask: SubTaskEntity)

    @Delete
    suspend fun deleteSubTask(subTask: SubTaskEntity)

    @Query("UPDATE tasks SET isCompleted = :isCompleted WHERE id = :taskId")
    suspend fun updateTaskStatus(taskId: Int, isCompleted: Boolean)

    @Query("UPDATE subtasks SET isCompleted = :isCompleted WHERE id = :subTaskId")
    suspend fun updateSubtaskStatus(subTaskId: Int, isCompleted: Boolean)
}
