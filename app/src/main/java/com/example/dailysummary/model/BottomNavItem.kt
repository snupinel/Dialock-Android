package com.example.dailysummary.model

import androidx.compose.ui.graphics.vector.ImageVector

data class BottomNavItem(
    val title:String,
    val tag: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    //val pageRoute: String,
    val badgeAmount: Int? = null
)