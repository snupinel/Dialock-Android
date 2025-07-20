package com.example.dailysummary.components

import androidx.compose.foundation.layout.Box
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.outlined.Alarm
import androidx.compose.material.icons.outlined.ArrowBackIosNew
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.Image
import androidx.compose.material.icons.outlined.Menu
import androidx.compose.material.icons.outlined.MoreVert
import androidx.compose.material.icons.outlined.Replay
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier

@Composable
fun BackButton(
    modifier: Modifier=Modifier,
    onClick:()->Unit,
){

    IconButton(
        modifier = modifier,
        onClick = onClick
    ) {
        Icon(imageVector = Icons.Outlined.ArrowBackIosNew, contentDescription = "Back")
    }
}
@Composable
fun ImageButton(
    modifier: Modifier=Modifier,
    onClick:()->Unit,
){
    IconButton(modifier = modifier,onClick =  { onClick() }) {
        Icon(imageVector = Icons.Outlined.Image, contentDescription = "Image")
    }
}

@Composable
fun MenuButton(
    modifier: Modifier=Modifier,
    onClick:()->Unit,
){
    IconButton(modifier = modifier,onClick =  { onClick() }) {
        Icon(imageVector = Icons.Outlined.Menu, contentDescription = "Menu")
    }
}
@Composable
fun DeleteButton(
    modifier: Modifier=Modifier,
    onClick:()->Unit,
){
    IconButton(modifier = modifier,onClick =  { onClick() }) {
        Icon(imageVector = Icons.Outlined.Delete, contentDescription = "Delete")
    }
}

@Composable
fun EditButton(
    onClick:()->Unit,
){
    IconButton(onClick =  { onClick() }) {
        Icon(imageVector = Icons.Outlined.Edit, contentDescription = "Edit")
    }
}

@Composable
fun ConfirmButton(
    onClick:()->Unit,
){
    IconButton(onClick =  { onClick() }) {
        Icon(imageVector = Icons.Outlined.Check, contentDescription = "Confirm")
    }
}
@Composable
fun RevertButton(
    enabled:Boolean = true,
    onClick:()->Unit,
){
    IconButton(enabled = enabled, onClick =  { onClick() }) {
        Icon(imageVector = Icons.Outlined.Replay, contentDescription = "Revert")
    }
}

@Composable
fun AlarmButton(
    enabled:Boolean = true,
    onClick:()->Unit,
){
    IconButton(enabled = enabled, onClick =  { onClick() }) {
        Icon(imageVector = Icons.Outlined.Alarm, contentDescription = "Alarm")
    }
}

@Composable
fun MoreVertButton(
    onDeleteClicked: () -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }

    Box {
        IconButton(onClick = { expanded = true }) {
            Icon(
                imageVector = Icons.Default.MoreVert,
                contentDescription = "More options"
            )
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {

            DropdownMenuItem(
                text = { Text("삭제") },
                onClick = {
                    expanded = false
                    onDeleteClicked()
                }
            )
        }
    }
}

