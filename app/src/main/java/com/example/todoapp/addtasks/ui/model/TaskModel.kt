package com.example.todoapp.addtasks.ui.model

data class TaskModel(
    val id: Int = System.currentTimeMillis().hashCode(), //Obtener la fecha actual incluido los milisegundos
    val task: String,
    var selected:Boolean = false
)