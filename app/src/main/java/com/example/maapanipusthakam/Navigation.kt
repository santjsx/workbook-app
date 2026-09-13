package com.example.maapanipusthakam

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import com.example.maapanipusthakam.theme.BrickTerracotta
import com.example.maapanipusthakam.theme.DividerColor
import com.example.maapanipusthakam.theme.InkPrimary
import com.example.maapanipusthakam.theme.InkSecondary
import com.example.maapanipusthakam.theme.PaperBackground
import com.example.maapanipusthakam.theme.PaperCard
import com.example.maapanipusthakam.theme.PaperCardElevated
import com.example.maapanipusthakam.ui.history.WorkHistoryScreen
import com.example.maapanipusthakam.ui.home.HomeScreen
import com.example.maapanipusthakam.ui.search.SearchScreen
import com.example.maapanipusthakam.ui.settings.SettingsScreen
import com.example.maapanipusthakam.ui.work.NewWorkScreen
import com.example.maapanipusthakam.ui.work.WorkNotebookScreen

data class BottomNavItem(
    val key: AppNavKey,
    val label: String,
    val icon: ImageVector
)

@Composable
fun MainNavigation() {
    val backStack = rememberNavBackStack(HomeNavKey as AppNavKey)
    val currentKey = backStack.lastOrNull() ?: HomeNavKey

    val bottomNavItems = listOf(
        BottomNavItem(HomeNavKey, "ఈ రోజు", Icons.Default.Home),
        BottomNavItem(HistoryNavKey, "నా పనులు", Icons.Default.Book),
        BottomNavItem(SearchNavKey, "వెతుకు", Icons.Default.Search),
        BottomNavItem(SettingsNavKey, "సదుపాయాలు", Icons.Default.Settings)
    )

    val isTopLevelDestination = currentKey in listOf(HomeNavKey, HistoryNavKey, SearchNavKey, SettingsNavKey)

    Scaffold(
        bottomBar = {
            if (isTopLevelDestination) {
                Surface(
                    color = PaperCard,
                    border = BorderStroke(1.dp, DividerColor),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    NavigationBar(
                        containerColor = PaperCard,
                        windowInsets = androidx.compose.material3.NavigationBarDefaults.windowInsets,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        bottomNavItems.forEach { item ->
                            val isSelected = currentKey == item.key
                            NavigationBarItem(
                                selected = isSelected,
                                onClick = {
                                    if (currentKey != item.key) {
                                        backStack.clear()
                                        backStack.add(item.key)
                                    }
                                },
                                icon = {
                                    Icon(
                                        imageVector = item.icon,
                                        contentDescription = item.label,
                                        modifier = Modifier.size(24.dp)
                                    )
                                },
                                label = {
                                    Text(
                                        text = item.label,
                                        style = MaterialTheme.typography.labelMedium.copy(
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                            fontSize = 13.sp
                                        )
                                    )
                                },
                                colors = NavigationBarItemDefaults.colors(
                                    selectedIconColor = BrickTerracotta,
                                    selectedTextColor = BrickTerracotta,
                                    indicatorColor = BrickTerracotta.copy(alpha = 0.12f),
                                    unselectedIconColor = InkSecondary,
                                    unselectedTextColor = InkSecondary
                                )
                            )
                        }
                    }
                }
            }
        },
        containerColor = PaperBackground
    ) { padding ->
        NavDisplay(
            backStack = backStack,
            onBack = {
                if (backStack.size > 1) {
                    backStack.removeLastOrNull()
                }
            },
            entryProvider = entryProvider {
                entry<HomeNavKey> {
                    HomeScreen(
                        onOpenWork = { workId ->
                            backStack.add(WorkNotebookNavKey(workId))
                        },
                        onAddNewWork = {
                            backStack.add(NewWorkNavKey)
                        }
                    )
                }

                entry<HistoryNavKey> {
                    WorkHistoryScreen(
                        onOpenWork = { workId ->
                            backStack.add(WorkNotebookNavKey(workId))
                        },
                        onAddNewWork = {
                            backStack.add(NewWorkNavKey)
                        }
                    )
                }

                entry<SearchNavKey> {
                    SearchScreen(
                        onOpenWork = { workId ->
                            backStack.add(WorkNotebookNavKey(workId))
                        }
                    )
                }

                entry<SettingsNavKey> {
                    SettingsScreen()
                }

                entry<NewWorkNavKey> {
                    NewWorkScreen(
                        onNavigateBack = {
                            backStack.removeLastOrNull()
                        },
                        onWorkCreated = { workId ->
                            backStack.removeLastOrNull()
                            backStack.add(WorkNotebookNavKey(workId))
                        }
                    )
                }

                entry<WorkNotebookNavKey> { key ->
                    WorkNotebookScreen(
                        workId = key.workId,
                        onNavigateBack = {
                            backStack.removeLastOrNull()
                        }
                    )
                }
            },
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        )
    }
}
