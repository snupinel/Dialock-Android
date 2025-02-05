package com.example.dailysummary.pages

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.rememberImagePainter
import com.example.dailysummary.components.ImagePager

/*
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