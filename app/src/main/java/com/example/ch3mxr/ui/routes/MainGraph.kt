package com.example.ch3mxr.ui.routes

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import androidx.navigation.toRoute
import com.example.ch3mxr.ui.components.ArScreen
import com.example.ch3mxr.ui.features.main.CourseScreen
import com.example.ch3mxr.ui.features.main.CreateGroupScreen
import com.example.ch3mxr.ui.features.main.EditGroupScreen
import com.example.ch3mxr.ui.features.main.GroupsScreen
import com.example.ch3mxr.ui.features.main.HomeScreen
import com.example.ch3mxr.ui.model.CourseCatalog
import com.example.ch3mxr.ui.features.SessionViewModel

fun NavGraphBuilder.mainGraph(
    navController: NavHostController,
    viewModel: SessionViewModel,
    arSupported: Boolean
) {

    navigation<Graph.Main>(
        startDestination = Routes.Home
    ) {
        composable<Routes.Home> {
            HomeScreen(
                onCourseClick = { courseId ->
                    navController.navigate(Routes.CourseDetail(courseId))
                },
                onLibresClick = {},
                onGruposClick = {
                    navController.navigate(Routes.Groups)
                },
                onLogoutClick = {
                    viewModel.clearSession()
                    navController.navigate(Graph.Auth) {
                        popUpTo(Graph.Main) {
                            inclusive = true
                        }
                    }
                },
                arSupported = arSupported
            )
        }
        composable<Routes.CourseDetail> {
            val route =
                it.toRoute<Routes.CourseDetail>()
            CourseScreen(
                course = CourseCatalog.byId(route.courseId),
                onBack = {
                    navController.popBackStack()
                }, onArClick = {
                    navController.navigate(Routes.ArScreen)
                }
            )
        }
        composable<Routes.ArScreen> {
            ArScreen(
                onBack = {
                    navController.popBackStack()
                }
            )
        }
        composable<Routes.Groups> {
            GroupsScreen(
                onGrupoClick = {},
                onLibresClick = {
                    navController.popBackStack()
                },
                onCreateClick = {
                    navController.navigate(Routes.CreateGroup)
                },
                onEditClick = { group ->
                    navController.navigate(
                        Routes.EditGroup(group)
                    )
                },
                onLogoutClick = {
                    viewModel.clearSession()
                    navController.navigate(Graph.Auth) {
                        popUpTo(Graph.Main) {
                            inclusive = true
                        }
                    }
                }
            )
        }
        composable<Routes.CreateGroup> {
            CreateGroupScreen(
                onBack = {
                    navController.popBackStack()
                }
            )
        }
        composable<Routes.EditGroup> {
            val route =
                it.toRoute<Routes.EditGroup>()
            EditGroupScreen(
                groupName = route.groupName,
                onBack = {
                    navController.popBackStack()
                }
            )
        }
    }
}