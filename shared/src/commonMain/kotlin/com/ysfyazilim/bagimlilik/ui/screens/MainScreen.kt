package com.opendrip.bagimlilik.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.opendrip.bagimlilik.data.repository.AuthRepository
import com.opendrip.bagimlilik.ui.screens.achievements.AchievementsScreen
import com.opendrip.bagimlilik.ui.screens.auth.AuthScreen
import com.opendrip.bagimlilik.ui.screens.challenge.ChallengeScreen
import com.opendrip.bagimlilik.ui.screens.home.HomeScreen
import com.opendrip.bagimlilik.ui.screens.profile.ProfileScreen
import com.opendrip.bagimlilik.ui.screens.stats.StatsScreen
import com.opendrip.bagimlilik.ui.screens.chat.ChatScreen

sealed class BottomNavItem(val title: String, val icon: ImageVector, val route: String) {
    object Home : BottomNavItem("Ana Sayfa", Icons.Default.Home, "home")
    object Challenge : BottomNavItem("Defterim", Icons.Default.Book, "challenge")
    object Stats : BottomNavItem("İstatistik", Icons.Default.BarChart, "stats")
    object AI : BottomNavItem("Yapay Zeka", Icons.Default.AutoAwesome, "ai")
    object Achievements : BottomNavItem("Başarımlar", Icons.Default.EmojiEvents, "achievements")
    object Profile : BottomNavItem("Profil", Icons.Default.Person, "profile")
}

@Composable
fun MainScreen() {
    val context = LocalContext.current
    val authRepository = remember { AuthRepository(context) }
    
    var showSplash by remember { mutableStateOf(true) }
    var isLoggedIn by remember { mutableStateOf(authRepository.isLoggedIn()) }
    
    var selectedItem by remember { mutableIntStateOf(0) }
    val items = listOf(
        BottomNavItem.Home,
        BottomNavItem.Challenge,
        BottomNavItem.Stats,
        BottomNavItem.AI,
        BottomNavItem.Achievements,
        BottomNavItem.Profile
    )

    if (showSplash) {
        SplashScreen(onFinished = { showSplash = false })
    } else if (!isLoggedIn) {
        AuthScreen(onLoginSuccess = { isLoggedIn = true })
    } else {
        Scaffold(
            bottomBar = {
                NavigationBar(
                    containerColor = MaterialTheme.colorScheme.surface,
                    tonalElevation = 8.dp
                ) {
                    items.forEachIndexed { index, item ->
                        NavigationBarItem(
                            icon = { Icon(item.icon, contentDescription = item.title) },
                            label = { Text(item.title, fontSize = 7.5.sp, maxLines = 1, softWrap = false, style = MaterialTheme.typography.labelSmall) },
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
                    4 -> AchievementsScreen()
                    5 -> ProfileScreen()
                    else -> HomeScreen()
                }
            }
        }
    }
}
