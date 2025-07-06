package com.example.dailysummary.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBackIosNew
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.Image
import androidx.compose.material.icons.outlined.Menu
import androidx.compose.material.icons.outlined.Replay
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
@Composable
fun ImageButton(
    modifier: Modifier=Modifier,
    onClick:()->Unit,
){
    IconButton(modifier = modifier,onClick = onClick) {
        Icon(imageVector = Icons.Outlined.Image, contentDescription = "Image")
    }
}

@Composable
fun MenuButton(
    modifier: Modifier=Modifier,
    onClick:()->Unit,
){
    IconButton(modifier = modifier,onClick = onClick) {
        Icon(imageVector = Icons.Outlined.Menu, contentDescription = "Menu")
    }
}
@Composable
fun DeleteButton(
    modifier: Modifier=Modifier,
    onClick:()->Unit,
){
    IconButton(modifier = modifier,onClick = onClick) {
        Icon(imageVector = Icons.Outlined.Delete, contentDescription = "Delete")
    }
}

@Composable
fun EditButton(
    onClick:()->Unit,
){
    IconButton(onClick = onClick) {
        Icon(imageVector = Icons.Outlined.Edit, contentDescription = "Edit")
    }
}

@Composable
fun ConfirmButton(
    onClick:()->Unit,
){
    IconButton(onClick = onClick) {
        Icon(imageVector = Icons.Outlined.Check, contentDescription = "Confirm")
    }
}
@Composable
fun RevertButton(
    enabled:Boolean = true,
    onClick:()->Unit,
){
    IconButton(enabled = enabled, onClick = onClick) {
        Icon(imageVector = Icons.Outlined.Replay, contentDescription = "Revert")
    }
}
