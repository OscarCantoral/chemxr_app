package com.example.ch3mxr.ui.routes

import kotlinx.serialization.Serializable

sealed interface Graph {

    @Serializable
    data object App : Graph

    @Serializable
    data object Auth : Graph

    @Serializable
    data object Main : Graph
}

sealed interface Routes {

    @Serializable
    data object Loading : Routes

    @Serializable
    data object Login : Routes

    @Serializable
    data object Register : Routes

    @Serializable
    data object Home : Routes

    @Serializable
    data class CourseDetail(
        val courseId: String
    ) : Routes

    @Serializable
    data object Groups : Routes

    @Serializable
    data object CreateGroup : Routes

    @Serializable
    data object ArScreen: Routes

    @Serializable
    data class EditGroup(
        val groupName: String
    ) : Routes
}