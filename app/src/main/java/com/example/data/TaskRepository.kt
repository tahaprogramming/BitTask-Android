package com.example.data

import kotlinx.coroutines.flow.Flow

class TaskRepository(private val taskDao: TaskDao) {
    val allTasks: Flow<List<TaskEntity>> = taskDao.getAllTasks()

    fun getSubtasks(taskId: Int): Flow<List<SubTaskEntity>> = taskDao.getSubTasksForTask(taskId)

    suspend fun getTaskById(taskId: Int): TaskEntity? = taskDao.getTaskById(taskId)

    suspend fun getSubtasksSync(taskId: Int): List<SubTaskEntity> = taskDao.getSubTasksForTaskSync(taskId)

    suspend fun insertTask(task: TaskEntity): Long = taskDao.insertTask(task)

    suspend fun updateTask(task: TaskEntity) = taskDao.updateTask(task)

    suspend fun deleteTask(task: TaskEntity) = taskDao.deleteTask(task)

    suspend fun insertSubTask(subTask: SubTaskEntity): Long = taskDao.insertSubTask(subTask)

    suspend fun updateSubTask(subTask: SubTaskEntity) = taskDao.updateSubTask(subTask)

    suspend fun deleteSubTask(subTask: SubTaskEntity) = taskDao.deleteSubTask(subTask)

    suspend fun updateTaskStatus(taskId: Int, isCompleted: Boolean) = taskDao.updateTaskStatus(taskId, isCompleted)

    suspend fun updateSubtaskStatus(subTaskId: Int, isCompleted: Boolean) = taskDao.updateSubtaskStatus(subTaskId, isCompleted)
}
