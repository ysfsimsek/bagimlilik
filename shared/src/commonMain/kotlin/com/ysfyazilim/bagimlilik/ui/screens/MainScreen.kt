package com.ysfyazilim.bagimlilik.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

import com.ysfyazilim.bagimlilik.ui.screens.home.HomeScreen
import com.ysfyazilim.bagimlilik.ui.screens.challenge.ChallengeScreen
import com.ysfyazilim.bagimlilik.ui.screens.stats.StatsScreen
import com.ysfyazilim.bagimlilik.ui.screens.chat.ChatScreen
import com.ysfyazilim.bagimlilik.ui.screens.profile.ProfileScreen

sealed class BottomNavItem(val title: String, val icon: ImageVector) {
    object Home : BottomNavItem("Ana Sayfa", Icons.Default.Home)
    object Challenge : BottomNavItem("Defterim", Icons.Default.Book)
    object Stats : BottomNavItem("İstatistik", Icons.Default.BarChart)
    object AI : BottomNavItem("Yapay Zeka", Icons.Default.AutoAwesome)
    object Profile : BottomNavItem("Profil", Icons.Default.Person)
}

@Composable
fun MainScreen() {
    var selectedItem by remember { mutableIntStateOf(0) }
    val items = listOf(
        BottomNavItem.Home,
        BottomNavItem.Challenge,
        BottomNavItem.Stats,
        BottomNavItem.AI,
        BottomNavItem.Profile
    )

    Scaffold(
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surface,
                tonalElevation = 8.dp
            ) {
                items.forEachIndexed { index, item ->
                    NavigationBarItem(
                        icon = { Icon(item.icon, contentDescription = item.title) },
                        label = { Text(item.title, fontSize = 9.sp, maxLines = 1, style = MaterialTheme.typography.labelSmall) },
                        selected = selectedItem == index,
                        onClick = { selectedItem = index },
                        alwaysShowLabel = true,
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = MaterialTheme.colorScheme.primary,
                            selectedTextColor = MaterialTheme.colorScheme.primary,
                            indicatorColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                        )
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            when (selectedItem) {
                0 -> HomeScreen()
                1 -> ChallengeScreen()
                2 -> StatsScreen()
                3 -> ChatScreen()
                4 -> ProfileScreen()
                else -> HomeScreen()
            }
        }
    }
}