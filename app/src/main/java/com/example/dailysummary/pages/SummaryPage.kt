package com.example.dailysummary.pages

import android.net.Uri
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActionScope
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CameraAlt
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.Replay
import androidx.compose.material3.AlertDialogDefaults.containerColor
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarDefaults.topAppBarColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInWindow
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntRect
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.round
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupPositionProvider
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.rememberImagePainter
import com.example.dailysummary.components.BackButton
import com.example.dailysummary.components.ConfirmButton
import com.example.dailysummary.components.EditButton
import com.example.dailysummary.components.ImagePager
import com.example.dailysummary.components.RevertButton
import com.example.dailysummary.components.SaveButton
import com.example.dailysummary.ui.theme.DailySummaryTheme
import com.example.dailysummary.viewModel.SummaryPageViewModel
import kotlinx.coroutines.android.awaitFrame
import java.time.LocalDate


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun SummaryPage(
    navController: NavController, year:Int,month:Int,day:Int){

    val viewModel = hiltViewModel<SummaryPageViewModel>()

    val images by viewModel.images.collectAsState()


    LaunchedEffect(true) {
        viewModel.initialize(year, month, day)
    }

    val listState = rememberLazyListState()
    //val imgHeight = 400
    val alpha = remember {
        derivedStateOf {
            if (listState.layoutInfo.visibleItemsInfo.firstOrNull() == null)
                0f
            else if (listState.firstVisibleItemIndex == 0)
                (listState.firstVisibleItemScrollOffset.toFloat() / listState.layoutInfo.visibleItemsInfo.firstOrNull()!!.size)
            else 1f
        }
    }

    //dpToPixel(imgHeight.toFloat(),)
    /*
    val alpha by remember {
        derivedStateOf {
            // Calculate the alpha based on the scroll offset
            // Coerce the value to be between 0f and 1f
            (
                    //1f-(listState.layoutInfo.visibleItemsInfo.firstOrNull()?.size?:0)/imgHeight

            ).toFloat()//.coerceIn(0f, 1f)
        }
    }*/
    Scaffold(
        bottomBar = {SummaryPageBottomBar(

        ){
            viewModel.insertSummary()
            navController.popBackStack()
        } },
        topBar = {
            SummaryPageToolbar(
                alpha = alpha.value,
                year,month,day,
            ){
                navController.popBackStack()
            }

        }) { paddingValues ->

        LazyColumn(
            state = listState,
            modifier = Modifier
                .padding(paddingValues.calculateBottomPadding())
        ) {
            item {
                ImagePager(
                    images = images, modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(1f)
                )
            }
            item{
                ImagePart()
                TitlePart()
                Text("${year}년 ${month}월 ${day}일")
                ContentPart()


            }

        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ImagePart(){

    val viewModel= hiltViewModel<SummaryPageViewModel>()

    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.GetMultipleContents()) { uris: List<Uri?> ->
        viewModel.setImages(uris.filterNotNull())   // 선택한 이미지 URI 저장
    }

    Box(
        Modifier
            .size(80.dp)
            .clip(RoundedCornerShape(5.dp))
            .border(
                width = 2.dp,
                color = Color.Gray,
                shape = RoundedCornerShape(7.dp)
            )
            .clickable {
                launcher.launch("image/*")
            },
        contentAlignment = Alignment.Center
    ){
        Column(horizontalAlignment = Alignment.CenterHorizontally){
            Icon(
                imageVector = Icons.Outlined.CameraAlt,
                contentDescription = "CameraAlt"
            )
            //Text("${uploadImages.size}/10")
        }
    }
}


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun SummaryPageBottomBar(
    onSave:()->Unit,
){
    val viewModel = hiltViewModel<SummaryPageViewModel>()

    val isWritten by viewModel.isWritten.collectAsState()
    if(isWritten){

    }
    else{
        SaveButton {
            onSave()
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun TitlePart(){
    val viewModel = hiltViewModel<SummaryPageViewModel>()

    val isWritten by viewModel.isWritten.collectAsState()

    val title = viewModel.summary.collectAsState().value.title
    val isEditingTitle by viewModel.isEditingTitle.collectAsState()
    var editTitleValue by rememberSaveable {
        mutableStateOf("")
    }

    val focusRequester = remember { FocusRequester() }
    val keyboardController = LocalSoftwareKeyboardController.current
    val localFocusManager = LocalFocusManager.current
    val titleScrollState = rememberScrollState()


    LaunchedEffect(isEditingTitle) {
        editTitleValue=title
        if (isEditingTitle) {
            //awaitFrame() // ✅ UI가 완전히 렌더링된 후 실행 보장
            focusRequester.requestFocus()  // 🚀 TextField가 렌더링된 후 포커스
            keyboardController?.show()  // 🚀 키보드 자동 표시
        }
    }

    EditAbleTextPart(
        isWritten = isWritten,
        isEditing = isEditingTitle,
        value = title,
        editValue = editTitleValue,
        onEditValueChange = {editTitleValue=it},
        focusRequester = focusRequester,
        singleLine = true,
        localFocusManager = localFocusManager,
        hint = "제목",
        setIsEditing = { viewModel.setIsEditingTitle(it) },
        updateByDate = { viewModel.updateTitleByDate() },
        saveText = {viewModel.setTitle(it)}
    )


}



@Composable
fun EditAbleTextPart(
    isWritten:Boolean,
    isEditing:Boolean,
    value:String,
    editValue:String,
    onEditValueChange:(String)->Unit,
    focusRequester:FocusRequester,
    singleLine:Boolean,
    localFocusManager:FocusManager,
    onDone: (KeyboardActionScope.() -> Unit)? = {localFocusManager.clearFocus()},
    hint:String,
    setIsEditing:(Boolean)->Unit,
    updateByDate:()->Unit,
    saveText:(String)->Unit,
){
    if(isEditing||!isWritten){
        Column {
            BasicTextField(
                value = if (isWritten) editValue else value,
                onValueChange = {
                    if (isWritten) onEditValueChange(it)
                    else saveText(it) },
                modifier = Modifier
                    .focusRequester(focusRequester),
                singleLine=singleLine,
                keyboardOptions = KeyboardOptions.Default.copy(
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(
                    onDone = onDone
                ),
                textStyle = TextStyle(color = MaterialTheme.colorScheme.primaryContainer),
                decorationBox = {
                        innerTextField ->
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(max = 45.dp)
                            .clip(RoundedCornerShape(5.dp))
                            .border(
                                width = 2.dp,
                                color = MaterialTheme.colorScheme.primary,
                                shape = RoundedCornerShape(7.dp)
                            )
                            .padding(12.dp)
                    ){
                        if(isWritten){
                            if(editValue.isEmpty()){
                                Text(hint, color = Color.Gray)
                            }
                        }
                        else{
                            if(value.isEmpty()){
                                Text(hint, color = Color.Gray)
                            }
                        }

                        innerTextField()
                    }
                }
            )
            if(isWritten){
                Row(horizontalArrangement = Arrangement.SpaceBetween){
                    RevertButton {
                        setIsEditing(false)
                    }
                    ConfirmButton {
                        saveText(editValue)
                        updateByDate()
                        setIsEditing(false)
                    }
                }
            }

        }
    }
    else{
        Row(verticalAlignment = Alignment.Bottom) {
            Text(text=value, fontSize = 24.sp, fontWeight = FontWeight.Bold)
            EditButton {

                setIsEditing(true)
            }
        }
    }
}




@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SummaryPageToolbar(
    alpha: Float, year: Int,month: Int,day: Int,
    onNav:()->Unit,
    ) {
    val interpolatedColor = lerp(Color.White, Color.Black, alpha)
    TopAppBar(

        title = {  },
        navigationIcon = {
            Row {
                BackButton{
                    onNav()
                }
                //HomeButton(navController = navController)
            }
        },
        actions = {
            //ShareButton()
            //MoreVertButton()
        },


        colors = topAppBarColors(
            containerColor = Color.White.copy(alpha = alpha),
            scrolledContainerColor = MaterialTheme.colorScheme.surfaceVariant,
            navigationIconContentColor = interpolatedColor,
            titleContentColor = interpolatedColor, // Color for the title
            actionIconContentColor = interpolatedColor // Color for action icons
        ),
    )



}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SummaryPageTopAppBar(
    year: Int,month: Int,day: Int,
    onNav:()->Unit,
    ){
    TopAppBar(
        title = {  },
        navigationIcon = {
            BackButton {
                onNav()
            }
        }

    )
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ContentPart(){
    val viewModel = hiltViewModel<SummaryPageViewModel>()

    val isWritten by viewModel.isWritten.collectAsState()

    val content = viewModel.summary.collectAsState().value.content
    val isEditingContent by viewModel.isEditingContent.collectAsState()
    var editContentValue by rememberSaveable {
        mutableStateOf("")
    }

    val focusRequester = remember { FocusRequester() }
    val keyboardController = LocalSoftwareKeyboardController.current
    val localFocusManager = LocalFocusManager.current


    LaunchedEffect(isEditingContent) {
        editContentValue=content
        if (isEditingContent) {
            focusRequester.requestFocus()  // 🚀 TextField가 렌더링된 후 포커스
            keyboardController?.show()  // 🚀 키보드 자동 표시
        }
    }

    EditAbleTextPart(
        isWritten = isWritten,
        isEditing = isEditingContent,
        value = content,
        editValue = editContentValue,
        onEditValueChange = {editContentValue=it},
        focusRequester = focusRequester,
        singleLine = false,
        localFocusManager = localFocusManager,
        hint = "상세 내용",
        setIsEditing = {viewModel.setIsEditingContent(it)},
        updateByDate = { viewModel.updateContentByDate() },
        saveText = { viewModel.setContent(it) }
    )


}