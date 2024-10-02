package com.example.todoapp.settings.firestore.data

import android.util.Log
import com.example.todoapp.addtasks.data.TaskDao
import com.example.todoapp.addtasks.data.TaskEntity
import com.example.todoapp.taskcategory.data.CategoryDao
import com.example.todoapp.taskcategory.data.CategoryEntity
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FirebaseRepository @Inject constructor(
    private val categoryDao: CategoryDao,
    private val taskDao: TaskDao,
) {

    private val firestore = FirebaseFirestore.getInstance()

    private fun saveTaskToFirestore(taskEntity: TaskEntity) {
        val taskRef = firestore.collection("tasks").document(taskEntity.id)
        taskRef.set(taskEntity.toMap())
            .addOnSuccessListener { Log.d("FirebaseRepository", "Tarea guardada exitosamente") }
            .addOnFailureListener { e ->
                Log.e(
                    "FirebaseRepository",
                    "Error al guardar la tarea",
                    e
                )
            }
    }

    private fun saveCategoryToFirestore(categoryEntity: CategoryEntity) {
        val categoryRef = firestore.collection("categories").document(categoryEntity.id)
        categoryRef.set(categoryEntity)
            .addOnSuccessListener { Log.d("FirebaseRepository", "Categoría guardada exitosamente") }
            .addOnFailureListener { e ->
                Log.e(
                    "FirebaseRepository",
                    "Error al guardar la categoría",
                    e
                )
            }
    }

    // Sincronizar datos con Firestore (guardando datos locales en Firestore)
    suspend fun syncDataWithFirebase() {
        categoryDao.getCategory().first().forEach { categoryEntity ->
            saveCategoryToFirestore(categoryEntity)
        }

        taskDao.getTasks().first().forEach { taskEntity ->
            saveTaskToFirestore(taskEntity)
        }
    }

    // Sincronizar datos desde Firestore (obteniendo datos de Firestore a la base de datos local)
    @OptIn(DelicateCoroutinesApi::class)
    suspend fun syncDataFromFirestore() {
        try {
            // Obtener categorías desde Firestore y manejarlas
            firestore.collection("categories").get()
                .addOnSuccessListener { result ->
                    val categories = result.map { it.toObject(CategoryEntity::class.java) }
                    GlobalScope.launch {
                        handleCategories(categories)
                    }
                }
                .addOnFailureListener { exception ->
                    Log.e("FirebaseRepository", "Error al obtener categorías", exception)
                }

            // Fetch tasks from Firestore and handle them
            firestore.collection("tasks").get()
                .addOnSuccessListener { result ->
                    val tasks = result.map { TaskEntity.fromMap(it.data) }
                    GlobalScope.launch {
                        handleTasks(tasks)
                    }
                }
                .addOnFailureListener { exception ->
                    Log.e("FirebaseRepository", "Error al obtener tareas", exception)
                }
        } catch (e: Exception) {
            Log.e("FirebaseRepository", "Error en la sincronización de datos desde Firestore", e)
        }
    }

    // Manejar categorías obtenidas de Firestore
    private suspend fun handleCategories(categories: List<CategoryEntity>) {
        categories.forEach { categoryEntity ->
            try {
                val existingCategory = categoryDao.getCategoryById(categoryEntity.id)
                if (existingCategory == null) {
                    categoryDao.addCategory(categoryEntity)
                    Log.d("FirebaseRepository", "Categoria añadida: ${categoryEntity.category}")
                } else {
                    Log.d(
                        "FirebaseRepository",
                        "La categoría ya existe: ${categoryEntity.category}"
                    )
                }
            } catch (e: Exception) {
                Log.e(
                    "FirebaseRepository",
                    "Error al agregar categoría: ${categoryEntity.category}",
                    e
                )
            }
        }
    }

    // Manejar tareas obtenidas de Firestore
    private suspend fun handleTasks(tasks: List<TaskEntity>) {
        tasks.forEach { taskEntity ->
            try {
                val existingTask = taskDao.getTaskById(taskEntity.id)
                if (existingTask == null || existingTask.task != taskEntity.task) {
                    taskDao.addTask(taskEntity)
                    Log.d("FirebaseRepository", "Tarea añadida: ${taskEntity.task}")
                } else {
                    Log.d("FirebaseRepository", "La tarea ya existe: ${taskEntity.task}")
                }
            } catch (e: Exception) {
                Log.e("FirebaseRepository", "Error al agregar tarea: ${taskEntity.task}", e)
            }
        }
    }
}