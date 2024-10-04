package com.example.todoapp.state.data

import com.example.todoapp.state.data.constants.DefaultStateId

fun seedStates() = listOf(
    StateEntity(id = DefaultStateId.ACTIVE_ID, state = DefaultStateId.ACTIVE_STATE),
    StateEntity(id = DefaultStateId.DELETED_ID, state = DefaultStateId.DELETED_STATE),
    StateEntity(id = DefaultStateId.UPDATED_ID, state = DefaultStateId.UPDATED_STATE),
    StateEntity(id = DefaultStateId.ERROR_ID, state = DefaultStateId.ERROR_STATE)
)