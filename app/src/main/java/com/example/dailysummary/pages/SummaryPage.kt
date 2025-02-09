package com.example.dailysummary.pages

import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import com.example.dailysummary.components.ImagePager
import com.example.dailysummary.components.SaveButton
import com.example.dailysummary.ui.theme.DailySummaryTheme
import com.example.dailysummary.viewModel.SummaryPageViewModel
import kotlinx.coroutines.android.awaitFrame
import java.time.LocalDate


@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SummaryPage(
    navController: NavController, year:Int,month:Int,day:Int){

    val viewModel = hiltViewModel<SummaryPageViewModel>()

    val title by viewModel.title.collectAsState()
    val editTitleValue by viewModel.editTitleValue.collectAsState()

    LaunchedEffect(true) {
        viewModel.initialize(year, month, day)
    }

    val listState = rememberLazyListState()
    val imgHeight = 400
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
        bottomBar = { },
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
                .padding(paddingValues)
                .padding(8.dp)
        ) {
            item {
                /*
                ImagePager(
                    images = goodsPostContent.images, modifier = Modifier
                        .fillMaxWidth()
                        .height(imgHeight.dp)
                )*/
            }
            item {
                Text("${year}년 ${month}월 ${day}일")
                TitlePart()



            }

        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun TitlePart(){
    val viewModel = hiltViewModel<SummaryPageViewModel>()

    val title by viewModel.title.collectAsState()

    var isTitleFocused by remember { mutableStateOf(false) }
    val focusRequester = remember { FocusRequester() }
    val keyboardController = LocalSoftwareKeyboardController.current
    val localFocusManager = LocalFocusManager.current

    val editTitleValue by viewModel.editTitleValue.collectAsState()

    val titleScrollState = rememberScrollState()

    val isEditingTitle by viewModel.isEditingTitle.collectAsState()

    LaunchedEffect(isEditingTitle) {
        if (isEditingTitle) {
            //awaitFrame() // ✅ UI가 완전히 렌더링된 후 실행 보장
            focusRequester.requestFocus()  // 🚀 TextField가 렌더링된 후 포커스
            keyboardController?.show()  // 🚀 키보드 자동 표시
        }
    }

    if(isEditingTitle){
        Column {
            BasicTextField(
                value = editTitleValue,
                onValueChange = { viewModel.setEditTextValue(it)},
                modifier = Modifier
                    .focusRequester(focusRequester)
                    .onFocusChanged { focusState ->
                        isTitleFocused = focusState.isFocused
                    },
                singleLine=true,
                keyboardOptions = KeyboardOptions.Default.copy(
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(
                    onDone = { localFocusManager.clearFocus() }
                ),
                decorationBox = {
                        innerTextField ->
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(max = 45.dp)
                            .clip(RoundedCornerShape(5.dp))
                            .border(
                                width = 2.dp,
                                color = if (isTitleFocused) Color.Black else Color.Gray,
                                shape = RoundedCornerShape(7.dp)
                            )
                            .padding(12.dp)
                            .horizontalScroll(titleScrollState),
                    ){
                        if(editTitleValue.isEmpty()){
                            Text("제목", color = Color.Gray)
                        }
                        innerTextField()
                    }
                }
            )
            Row{
                RevertButton {
                    viewModel.setIsEditingTitle(false)
                }
                ConfirmButton {
                    viewModel.setTitle(editTitleValue)
                    viewModel.updateTitleByDate()
                    viewModel.setIsEditingTitle(false)
                }
            }
        }
    }
    else{
        Row(verticalAlignment = Alignment.Bottom) {
            Text(text=title, fontSize = 24.sp, fontWeight = FontWeight.Bold)
            EditButton {

                viewModel.setIsEditingTitle(true)
            }
        }
    }


}



@Composable
fun EditTextField(){

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
    onClick:()->Unit,
){
    IconButton(onClick = onClick) {
        Icon(imageVector = Icons.Outlined.Replay, contentDescription = "Revert")
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SummaryPageToolbar(
    alpha: Float, year: Int,month: Int,day: Int,
    onNav:()->Unit,
    ) {
    //val interpolatedColor = lerp(Color.White, Color.Black, alpha)
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

        /*
        colors = topAppBarColors(
            containerColor = Color.White.copy(alpha = alpha),
            scrolledContainerColor = MaterialTheme.colorScheme.surfaceVariant,
            navigationIconContentColor = interpolatedColor,
            titleContentColor = interpolatedColor, // Color for the title
            actionIconContentColor = interpolatedColor // Color for action icons
        ),*/
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

/*

@Composable
fun WriteGoodsPostPage(viewModel: MainViewModel,navController: NavController){
    val context=LocalContext.current

    val uploadImages by viewModel.uploadImages.collectAsState()

    var title by rememberSaveable { mutableStateOf("") }
    var isTitleFocused by remember { mutableStateOf(false) }
    val titleScrollState = rememberScrollState()
    var sellPrice by rememberSaveable { mutableStateOf("") }
    var isSellPriceFocused by remember { mutableStateOf(false) }
    var offerYn by rememberSaveable { mutableStateOf(false) }
    var description by rememberSaveable { mutableStateOf("") }
    var isDescriptionFocused by remember { mutableStateOf(false) }
    val localFocusManager = LocalFocusManager.current

    val (permissionRequested, setPermissionRequested) = remember { mutableStateOf(false) }

    val allPermissionsGranted = viewModel.neededStoragePermissions().all {
        ContextCompat.checkSelfPermission(LocalContext.current, it) == PackageManager.PERMISSION_GRANTED
    }

    val multiplePermissionsLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions(),
    ) { permissions ->
        // 권한 요청 결과 처리. permissions는 Map<String, Boolean> 형태입니다.
        if(permissions.entries.all { it.value }){
            Log.d("aaaa","all_granted")
            navController.navigate("GalleryViewPage")
        }
    }
    LaunchedEffect(permissionRequested) {
        if (permissionRequested) {
            if (allPermissionsGranted) {
                // 모든 권한이 이미 부여되었을 경우의 처리
                Log.d("aaaa","already_granted")
                navController.navigate("GalleryViewPage")
            } else {
                // 하나 이상의 권한이 부여되지 않았을 경우 권한 요청 로직
                multiplePermissionsLauncher.launch(viewModel.neededStoragePermissions())
            }

            setPermissionRequested(false) // 상태를 다시 초기화
        }
    }

    Scaffold(bottomBar = {
        Box (modifier = Modifier
            .fillMaxWidth()
            .height(80.dp)
            .padding(horizontal = 16.dp, vertical = 10.dp)
            .clip(RoundedCornerShape(5.dp))
            .border(width = 2.dp, color = Color.Gray, shape = RoundedCornerShape(7.dp))
            .clickable {
                if (title.isNotEmpty() && sellPrice.isNotEmpty() && description.isNotEmpty()) {
                    CoroutineScope(Dispatchers.IO).launch {
                        try {
                            val images=if(viewModel.uploadImages.value.isNotEmpty())viewModel.uploadImages(uploadImages,context).images else null
                            //
                            Log.d("submitpost", "image success")
                            viewModel.submitPost(
                                SubmitPostRequest(
                                    areaId = viewModel.getRefAreaId()[0],
                                    title = title,
                                    description = description,
                                    type = if (sellPrice.toInt() == 0) "SHARE" else "TRADE",
                                    repImg=if(images!=null)images[0] else images,
                                    images = images,
                                    deadline = 0L,
                                    offerYn = offerYn,
                                    sellPrice = sellPrice.toInt()
                                )
                            )
                            withContext(Dispatchers.Main) {
                                //게시글 작성에 성공.
                                // 내가 쓴 글 페이지로 이동(현재 페이지를 stack에서 지우면서)
                                navController.popBackStack()
                            }
                        } catch (e: Exception) {
                            //
                            Log.d("submitpost", "submitpost failed:$e")
                        }
                    }
                }
            },
            contentAlignment = Alignment.Center){
            Text(text = "작성 완료", textAlign = TextAlign.Center)
        }
    }){paddingValues->
        LazyColumn(Modifier.padding(paddingValues)){
            item{
                TopAppBar(

                    title = {Text("당근")},
                    navigationIcon = {
                        Row{
                            BackButton(navController = navController)
                        }
                    },
                    actions = {
                        Text("임시저장")
                    },
                )
                Divider(
                    Modifier
                        .fillMaxWidth()
                        .height(1.dp))
                Column(Modifier.padding(16.dp)){
                    Row {
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
                                    setPermissionRequested(true)
                                },
                            contentAlignment = Alignment.Center
                        ){
                            Column(horizontalAlignment = Alignment.CenterHorizontally){
                                Icon(
                                    imageVector = Icons.Outlined.CameraAlt,
                                    contentDescription = "CameraAlt"
                                )
                                Text("${uploadImages.size}/10")
                            }
                        }
                        LazyRow{
                            items(uploadImages){  uri->
                                Spacer(modifier = Modifier.fillMaxHeight().width(16.dp))
                                Box(modifier = Modifier
                                    .size(80.dp)
                                    .clip(RoundedCornerShape(5.dp))
                                    .border(
                                        width = 2.dp,
                                        color = Color.Gray,
                                        shape = RoundedCornerShape(7.dp))
                                    .clickable {
                                        val updateList=uploadImages.toMutableList()
                                        updateList.remove(uri)
                                        viewModel.updateUploadImages(updateList)
                                    }){
                                    Image(
                                        painter = rememberImagePainter(data = uri),
                                        contentDescription = null,
                                        modifier = Modifier
                                            .fillMaxSize(),
                                        contentScale =  ContentScale.FillWidth
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier
                        .fillMaxWidth()
                        .height(40.dp))

                    Text("제목")
                    Spacer(modifier = Modifier
                        .fillMaxWidth()
                        .height(20.dp))
                    BasicTextField(
                        value = title,
                        onValueChange = { title = it},
                        modifier = Modifier.onFocusChanged { focusState ->
                            isTitleFocused = focusState.isFocused
                        },
                        singleLine=true,
                        keyboardOptions = KeyboardOptions.Default.copy(
                            imeAction = ImeAction.Next
                        ),
                        keyboardActions = KeyboardActions(
                            onNext = { localFocusManager.moveFocus(FocusDirection.Down) }
                        ),
                        decorationBox = {
                                innerTextField ->
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .heightIn(max = 45.dp)
                                    .clip(RoundedCornerShape(5.dp))
                                    .border(
                                        width = 2.dp,
                                        color = if (isTitleFocused) Color.Black else Color.Gray,
                                        shape = RoundedCornerShape(7.dp)
                                    )
                                    .padding(12.dp)
                                    .horizontalScroll(titleScrollState),
                            ){
                                if(title.isEmpty()){
                                    Text("제목", color = Color.Gray)
                                }
                                innerTextField()
                            }
                        }
                    )
                    LaunchedEffect(title) {
                        titleScrollState.scrollTo(titleScrollState.maxValue)
                    }
                    Spacer(modifier = Modifier
                        .fillMaxWidth()
                        .height(40.dp))

                    Text("거래 방식")
                    Spacer(modifier = Modifier
                        .fillMaxWidth()
                        .height(20.dp))
                    BasicTextField(
                        value = sellPrice,
                        onValueChange = {newText->
                            if (newText.all { it.isDigit() }&&newText.length<10) {
                                sellPrice = newText
                            }

                        },
                        modifier = Modifier.onFocusChanged { focusState ->
                            isSellPriceFocused = focusState.isFocused
                        },
                        keyboardOptions = KeyboardOptions.Default.copy(
                            imeAction = ImeAction.Next,
                            keyboardType = KeyboardType.Number,
                        ),
                        keyboardActions = KeyboardActions(
                            onNext = { localFocusManager.moveFocus(FocusDirection.Down) }
                        ),
                        decorationBox = {
                                innerTextField ->
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(5.dp))
                                    .border(
                                        width = 2.dp,
                                        color = if (isSellPriceFocused) Color.Black else Color.Gray,
                                        shape = RoundedCornerShape(7.dp)
                                    )
                                    .padding(12.dp),
                            ){
                                Row {
                                    Text("￦")
                                    Box{
                                        if(sellPrice.isEmpty()){
                                            Text("가격을 입력해주세요.", color = Color.Gray)
                                        }
                                        innerTextField()
                                    }
                                }

                            }
                        }
                    )
                    Spacer(modifier = Modifier
                        .fillMaxWidth()
                        .height(20.dp))
                    Row(verticalAlignment = Alignment.CenterVertically){
                        Checkbox(
                            checked = offerYn,
                            onCheckedChange = { offerYn = it }
                        )
                        Box(contentAlignment = Alignment.Center){Text("가격 제안 받기")}
                    }

                    Spacer(modifier = Modifier
                        .fillMaxWidth()
                        .height(40.dp))

                    Text("자세한 설명")
                    Spacer(modifier = Modifier
                        .fillMaxWidth()
                        .height(20.dp))
                    BasicTextField(
                        value = description,
                        onValueChange = { description = it},
                        modifier = Modifier.onFocusChanged { focusState ->
                            isDescriptionFocused = focusState.isFocused
                        },
                        keyboardOptions = KeyboardOptions.Default.copy(
                            //
                        ),
                        keyboardActions = KeyboardActions(
                            //
                        ),
                        decorationBox = {
                                innerTextField ->
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .heightIn(min = 160.dp)
                                    .clip(RoundedCornerShape(5.dp))
                                    .border(
                                        width = 2.dp,
                                        color = if (isDescriptionFocused) Color.Black else Color.Gray,
                                        shape = RoundedCornerShape(7.dp)
                                    )
                                    .padding(12.dp),
                            ){
                                if(description.isEmpty()){
                                    Text("(지역명)에 올릴 게시글 내용을 작성해 주세요.\n" +
                                            "(판매 금지 물품은 게시가 제한될 수 있어요.)\n" +
                                            "\n" +
                                            "신뢰할 수 있는 거래를 위해 자세히 적어주세요.\n" +
                                            "과학기술정보통신부, 한국 인터넷진흥원과 함께해요.", color = Color.Gray)
                                }
                                innerTextField()
                            }
                        }
                    )
                }
            }
        }
    }
}
@Composable
fun WriteSummaryPage() {


    LaunchedEffect(Unit) {
        //viewModel.updateGoodsPostContent(DefaultGoodsPostContentSample)
        //viewModel.getGoodsPostContent(id)
    }
    val listState = rememberLazyListState()
    val imgHeight = 400
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
        bottomBar = { },
        topBar = {
            GoodsPostToolbar(
                alpha = alpha.value,
                navController = navController
            )
        }) { paddingValues ->

        LazyColumn(
            state = listState,
            modifier = Modifier.padding(bottom = paddingValues.calculateBottomPadding())
        ) {
            item {
                ImagePager(
                    images = goodsPostContent.images, modifier = Modifier
                        .fillMaxWidth()
                        .height(imgHeight.dp)
                )
            }
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(100.dp)
                        .padding(start = 16.dp, end = 16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    val painter =
                        rememberImagePainter(data = if (goodsPostContent.profileImg != "") goodsPostContent.profileImg else "https://d1unjqcospf8gs.cloudfront.net/assets/users/default_profile_80-c649f052a34ebc4eee35048815d8e4f73061bf74552558bb70e07133f25524f9.png")
                    Image(
                        painter = painter,
                        contentDescription = null,
                        modifier = Modifier
                            .size(60.dp)
                            .border(
                                1.dp,
                                Color.Gray.copy(alpha = 0.2f),
                                shape = CircleShape
                            )
                            .clip(shape = CircleShape),
                        contentScale = ContentScale.Crop
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(text = goodsPostContent.authorName, fontWeight = FontWeight.Bold)
                        Text(text = goodsPostContent.sellingArea, color = Color.Gray, fontSize = 12.sp)
                    }
                    Column(horizontalAlignment = Alignment.End, modifier = Modifier.weight(1f)) {
                        val temp = goodsPostContent.authorMannerTemperature
                        val color = calculateMannerTempColor(temp)
                        val normalizedTemp = (temp - 30).coerceIn(0.0, 15.0) / 15f
                        Text(text = "${temp}도", color = color, fontSize = 14.sp)
                        LinearProgressIndicator(
                            progress = normalizedTemp.toFloat(), // Normalize to 0.0 to 1.0
                            modifier = Modifier
                                .width(48.dp)
                                .clip(CircleShape),
                            color = color
                        )
                    }
                }
                Column(modifier = Modifier.padding(start = 16.dp, end = 16.dp)) {
                    Divider(
                        modifier = Modifier
                            .height(1.dp)
                            .fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                    Text(
                        text = goodsPostContent.title,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                    )
                    Text(
                        text = formatProductTime(
                            goodsPostContent.createdAt,
                            goodsPostContent.refreshedAt
                        ),
                        fontSize = 15.sp,
                        color = Color.Gray,
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                    Text(text = goodsPostContent.description, fontSize = 20.sp, lineHeight = 30.sp)
                    Spacer(modifier = Modifier.height(24.dp))
                    Text(
                        text = (if (goodsPostContent.chatCnt > 0)
                            "채팅 ${goodsPostContent.chatCnt}·" else "") +
                                (if (goodsPostContent.wishCnt > 0)
                                    "관심 ${goodsPostContent.wishCnt}·" else "") +
                                "조회 ${goodsPostContent.viewCnt}",
                        fontSize = 15.sp, color = Color.Gray,
                    )
                    Spacer(modifier = Modifier.height(60.dp))
                }

            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GoodsPostToolbar(alpha: Float, navController: NavController) {
    val interpolatedColor = lerp(Color.White, Color.Black, alpha)
    TopAppBar(

        title = { },
        navigationIcon = {
            Row {
                BackButton(navController = navController)
                HomeButton(navController = navController)
            }
        },
        actions = {
            ShareButton()
            MoreVertButton()
        },

        colors = TopAppBarDefaults.smallTopAppBarColors(
            containerColor = Color.White.copy(alpha = alpha),
            navigationIconContentColor = interpolatedColor,
            titleContentColor = interpolatedColor, // Color for the title
            actionIconContentColor = interpolatedColor // Color for action icons
        ),
    )

}

@Composable

fun GoodsPostBottomBar(viewModel: MainViewModel, chatViewModel: ChatViewModel, goodsPostContent:GoodsPostContent, navController: NavController){
    Divider(modifier = Modifier
        .height(1.dp)
        .fillMaxWidth())
    Row(modifier = Modifier
        .fillMaxWidth()
        .height(80.dp)
        .padding(top = 16.dp, bottom = 16.dp),
        verticalAlignment = Alignment.CenterVertically){
        IconButton(onClick = {
            Log.d("aaaa", "wishToggle called")
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    viewModel.wishToggle(goodsPostContent.id, !goodsPostContent.isWish)
                    withContext(Dispatchers.Main) {
                        Log.d("aaaa", (!goodsPostContent.isWish).toString())
                        viewModel.updateGoodsPostContent(
                            goodsPostContent.copy(
                                isWish = !goodsPostContent.isWish,
                                wishCnt = goodsPostContent.wishCnt + if (goodsPostContent.isWish) -1 else 1
                            )
                        )
                    }
                    //api 통해서 wish 변화를 서버로 전달
                    //Log.d("aaaa",response.toString())
                } catch (e: Exception) {
                    Log.d("aaaa", "wishToggle failed:$e")
                }
            }


        }) {
            Icon(
                imageVector = if (goodsPostContent.isWish) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                contentDescription = "Wish",
                tint = bunnyColor,
            )
        }
        Divider(
            modifier = Modifier
                .width(1.dp)
                .fillMaxHeight()
        )
        Column(Modifier.padding(start = 16.dp), verticalArrangement = Arrangement.Center) {
            Text("${goodsPostContent.sellPrice}원", fontSize = 20.sp, fontWeight = FontWeight.Bold)
            Text(if (goodsPostContent.offerYn) "가격 제안 가능" else "가격 제안 불가", fontSize = 15.sp, color = Color.Gray)
        }
        if(goodsPostContent.type == "AUCTION"){
            Box(modifier = Modifier
                .padding(start = 12.dp, end = 16.dp)
                .height(50.dp)
                .width(100.dp)
                .clip(shape = RoundedCornerShape(4.dp))
                .background(color = bunnyColor)
                .clickable {
                    Log.d("aaaa123", goodsPostContent.id.toString() + goodsPostContent.maxBidPrice?.bidPrice.toString())
                    navController.navigate("AuctionPage?id=${goodsPostContent.id}&maxPrice=${goodsPostContent.maxBidPrice?.bidPrice}")
                }, contentAlignment = Alignment.Center
            ) {
                Text("경매 제안하기", color = Color.White)
            }
        } else{
            Spacer(modifier = Modifier.weight(1f))
        }
        Box(modifier = Modifier
            .height(50.dp)
            .width(90.dp)
            .clip(shape = RoundedCornerShape(4.dp))
            .background(color = bunnyColor)
            .clickable {
                if (goodsPostContent.authorId.toInt() != viewModel.userInfo.value.id) {
                    CoroutineScope(Dispatchers.IO).launch {
                        val channelId = chatViewModel.makeChatRoom(goodsPostContent.id)
                        Log.d("GoodsPostPage", "$channelId 의 channel 생성 완료")
                        withContext(Dispatchers.Main) {
                            navController.navigate("ChatRoomPage/${channelId}")
                        }
                    }
                }

            }, contentAlignment = Alignment.Center
        ) {
            Text("채팅하기", color = Color.White)
        }

    }
}*/