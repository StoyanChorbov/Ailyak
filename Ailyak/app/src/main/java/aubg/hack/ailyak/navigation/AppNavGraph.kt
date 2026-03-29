package aubg.hack.ailyak.navigation

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.Map
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.*
import aubg.hack.ailyak.ui.screens.AnimalIndexScreen
import aubg.hack.ailyak.ui.screens.MapScreen
import aubg.hack.ailyak.ui.screens.PlantIndexScreen
import aubg.hack.ailyak.ui.screens.SettingsScreen
import aubg.hack.ailyak.ui.screens.WaterIndexScreen

import androidx.compose.material.icons.filled.Eco
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Water
import androidx.compose.material.icons.filled.Pets
import androidx.compose.material.icons.outlined.Eco
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Water
import androidx.compose.material.icons.outlined.Pets
import aubg.hack.ailyak.ui.screens.ShelterIndexScreen

sealed class Screen(
    val route: String,
    val label: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
) {
    object Map : Screen(
        route = "map",
        label = "Map",
        selectedIcon = Icons.Filled.Map,
        unselectedIcon = Icons.Outlined.Map
    )
    object Plants : Screen(
        route = "plants",
        label = "Plants",
        selectedIcon = Icons.Filled.Eco,
        unselectedIcon = Icons.Outlined.Eco
    )
    object Water : Screen(
        route = "water",
        label = "Water",
        selectedIcon = Icons.Filled.Water,
        unselectedIcon = Icons.Outlined.Water
    )
    object Animals : Screen(
        route = "animals",
        label = "Animals",
        selectedIcon = Icons.Filled.Pets,
        unselectedIcon = Icons.Outlined.Pets
    )
    object Settings : Screen(
        route = "settings",
        label = "Settings",
        selectedIcon = Icons.Filled.Settings,
        unselectedIcon = Icons.Outlined.Settings
    )

    object Shelters : Screen(
        route = "shelters",
        label = "Shelters",
        selectedIcon = Icons.Filled.Home,
        unselectedIcon = Icons.Outlined.Home
    )
}

@Composable
fun AppNavGraph() {
    val navController = rememberNavController()
    val items = listOf(
        Screen.Plants,
        Screen.Water,
        Screen.Map,
        Screen.Animals,
        Screen.Settings
    )

    Scaffold(
        bottomBar = {
            NavigationBar {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentDestination = navBackStackEntry?.destination
                items.forEach { screen ->
                    val selected =
                        currentDestination?.hierarchy?.any { it.route == screen.route } == true
                    NavigationBarItem(
                        selected = selected,
                        onClick = {
                            navController.navigate(screen.route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        label = { Text(screen.label) },
                        icon = {
                            Icon(
                                imageVector = if (selected) screen.selectedIcon
                                else screen.unselectedIcon,
                                contentDescription = screen.label
                            )
                        }
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Map.route,
            modifier = Modifier.padding(innerPadding),
            enterTransition = {
                fadeIn(tween(250)) + slideInHorizontally(
                    initialOffsetX = { it / 4 },
                    animationSpec = tween(250)
                )
            },
            exitTransition = {
                fadeOut(tween(250)) + slideOutHorizontally(
                    targetOffsetX = { -it / 4 },
                    animationSpec = tween(250)
                )
            },
            popEnterTransition = {
                fadeIn(tween(250)) + slideInHorizontally(
                    initialOffsetX = { -it / 4 },
                    animationSpec = tween(250)
                )
            },
            popExitTransition = {
                fadeOut(tween(250)) + slideOutHorizontally(
                    targetOffsetX = { it / 4 },
                    animationSpec = tween(250)
                )
            }
        ) {
            composable(Screen.Map.route) { MapScreen() }
            composable(Screen.Plants.route) { PlantIndexScreen() }
            composable(Screen.Water.route) { WaterIndexScreen() }
            composable(Screen.Animals.route) { AnimalIndexScreen() }
            composable(Screen.Settings.route) { SettingsScreen() }
            composable(Screen.Shelters.route) { ShelterIndexScreen() }
        }
    }
}