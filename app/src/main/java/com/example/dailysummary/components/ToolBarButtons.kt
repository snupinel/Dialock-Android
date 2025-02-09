package com.example.dailysummary.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBackIosNew
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun BackButton(
    modifier: Modifier=Modifier,
    onClick:()->Unit,
){
    IconButton(modifier = modifier,onClick = onClick) {
        Icon(imageVector = Icons.Outlined.ArrowBackIosNew, contentDescription = "Back")
    }
}
