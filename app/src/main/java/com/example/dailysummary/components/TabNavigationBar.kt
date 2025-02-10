package com.example.dailysummary.components

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.dailysummary.model.BottomNavItem
import com.example.dailysummary.viewModel.MainPageViewModel
import com.example.dailysummary.viewModel.Tab

@Composable
fun TabNavigationBar(tabBarItems: List<BottomNavItem>) {
    val viewModel = hiltViewModel<MainPageViewModel>()

    val selectedTabPage by viewModel.selectedTab.collectAsState()
    NavigationBar(
        modifier = Modifier.height(50.dp+ WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()),
        containerColor = MaterialTheme.colorScheme.primaryContainer) {
        // looping over each tab to generate the views and navigation for each item
        tabBarItems.forEach{ tabBarItem ->
            NavigationBarItem(
                modifier = Modifier.fillMaxHeight(),
                selected = selectedTabPage == Tab.valueOf(tabBarItem.title),
                onClick = {
                    viewModel.updateTab(tabBarItem.title)
                },
                icon = {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        TabBarIconView(
                            isSelected = selectedTabPage == Tab.valueOf(tabBarItem.title),
                            selectedIcon = tabBarItem.selectedIcon,
                            unselectedIcon = tabBarItem.unselectedIcon,
                            title = tabBarItem.title,
                            badgeAmount = tabBarItem.badgeAmount
                        )
                        Text(tabBarItem.tag, fontSize = 10.sp)
                    }

                },
        ) }
    }
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TabBarIconView(
    isSelected: Boolean,
    selectedIcon: ImageVector,
    unselectedIcon: ImageVector,
    title: String,
    badgeAmount: Int? = null
) {
    Column{
        BadgedBox(badge = { TabBarBadgeView(badgeAmount) }) {
            Icon(
                imageVector = if (isSelected) {selectedIcon} else {unselectedIcon},
                contentDescription = title
            )
        }
    }

}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun TabBarBadgeView(count: Int? = null) {
    if (count != null) {
        Badge {
            Text(count.toString())
        }
    }
}