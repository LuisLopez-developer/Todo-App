package com.example.todoapp.settings.data

import android.util.Log
import com.example.todoapp.addtasks.data.TaskDao
import com.example.todoapp.addtasks.data.TaskEntity
import com.example.todoapp.taskcategory.data.CategoryDao
import com.example.todoapp.taskcategory.data.CategoryEntity
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FirebaseRepository @Inject constructor(
    private val categoryDao: CategoryDao,
    private val taskDao: TaskDao,
) {

    private val firestore = FirebaseFirestore.getInstance()

    fun saveTaskToFirestore(task: TaskEntity) {
        val taskRef = firestore.collection("tasks").document(task.id.toString())
        taskRef.set(task.toMap())
    }

    fun saveCategoryToFirestore(category: CategoryEntity) {
        val categoryRef = firestore.collection("categories").document(category.id.toString())
        categoryRef.set(category)
    }

    private fun getTasksFromFirestore(
        onSuccess: (List<TaskEntity>) -> Unit,
        onFailure: (Exception) -> Unit,
    ) {
        firestore.collection("tasks").get()
            .addOnSuccessListener { result ->
                val tasks = result.map { TaskEntity.fromMap(it.data) }
                onSuccess(tasks)
            }
            .addOnFailureListener { exception ->
                onFailure(exception)
            }
    }

    private fun getCategoriesFromFirestore(
        onSuccess: (List<CategoryEntity>) -> Unit,
        onFailure: (Exception) -> Unit,
    ) {
        firestore.collection("categories").get()
            .addOnSuccessListener { result ->
                val categories = result.map { it.toObject(CategoryEntity::class.java) }
                onSuccess(categories)
            }
            .addOnFailureListener { exception ->
                onFailure(exception)
            }
    }

    @OptIn(DelicateCoroutinesApi::class)
    suspend fun syncTasksFromFirestore() {
        try {
            getCategoriesFromFirestore(onSuccess = { categories ->
                GlobalScope.launch {
                    categories.forEach { categoryEntity ->
                        try {
                            val existingCategory = categoryDao.getCategoryById(categoryEntity.id)
                            if (existingCategory == null) {
                                categoryDao.addCategory(categoryEntity)
                                Log.d(
                                    "FirebaseRepository",
                                    "Category added: ${categoryEntity.category}"
                                )
                            } else {
                                Log.d(
                                    "FirebaseRepository",
                                    "Category already exists: ${categoryEntity.category}"
                                )
                            }
                        } catch (e: Exception) {
                            Log.e(
                                "FirebaseRepository",
                                "Error adding category: ${categoryEntity.category}",
                                e
                            )
                        }
                    }
                }

                getTasksFromFirestore(onSuccess = { tasks ->
                    GlobalScope.launch {
                        tasks.forEach { taskEntity ->
                            try {
                                val existingTask = taskDao.getTaskById(taskEntity.id)
                                if (existingTask == null || existingTask.task != taskEntity.task) {
                                    taskDao.addTask(taskEntity)
                                    Log.d("FirebaseRepository", "Task added: ${taskEntity.task}")
                                } else {
                                    Log.d(
                                        "FirebaseRepository",
                                        "Task already exists: ${taskEntity.task}"
                                    )
                                }
                            } catch (e: Exception) {
                                Log.e(
                                    "FirebaseRepository",
                                    "Error adding task: ${taskEntity.task}",
                                    e
                                )
                            }
                        }
                    }
                }, onFailure = {
                    Log.e("FirebaseRepository", "Error fetching tasks from Firestore", it)
                })
            }, onFailure = {
                Log.e("FirebaseRepository", "Error fetching categories from Firestore", it)
            })
        } catch (e: Exception) {
            Log.e("FirebaseRepository", "Error in syncTasksFromFirestore", e)
        }
    }
}