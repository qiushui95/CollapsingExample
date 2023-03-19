@file:OptIn(ExperimentalFoundationApi::class, InternalLandscapistApi::class)

package com.example.collapsingexample

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import com.example.collapsingexample.ui.theme.CollapsingExampleTheme
import com.skydoves.landscapist.ImageOptions
import com.skydoves.landscapist.InternalLandscapistApi
import com.skydoves.landscapist.glide.GlideImage
import kotlinx.coroutines.launch

private const val IMAGE_URL =
    "https://imgres.apkssr.com/upload/v4/img/2023/3/15/14/a60ec223-e10f-4b22-bd64-23b8eaf4e3dc.jpg"

private val defaultTopBarHeight = 155.dp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            CollapsingExampleTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {

                    val displayMetrics = LocalContext.current.resources.displayMetrics

                    val widthPixels = displayMetrics.widthPixels

                    val density = LocalDensity.current


                    CompositionLocalProvider(
                        LocalDensity provides Density(
                            widthPixels / 1080f,
                            fontScale = density.fontScale
                        )
                    ) {
                        MainPage()
                    }
                }
            }
        }
    }

}

@Composable
fun MainPage() {

    Column(modifier = Modifier.fillMaxSize()) {

        val pagerState = rememberPagerState()

        val coroutineScope = rememberCoroutineScope()

        HorizontalPager(
            pageCount = 2, modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            state = pagerState,
            userScrollEnabled = false
        ) {
            when (it) {
                0 -> MinePage()
                1 -> OthersPage()
            }
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(160.dp)
        ) {
            Tab("我的", pagerState.currentPage == 0) {
                coroutineScope.launch { pagerState.animateScrollToPage(0) }
            }
            Tab("他人", pagerState.currentPage == 1) {
                coroutineScope.launch { pagerState.animateScrollToPage(1) }
            }
        }
    }
}

@Composable
fun RowScope.Tab(tabText: String, isCurrent: Boolean, onClick: () -> Unit) {

    val textColor = remember(isCurrent) {
        if (isCurrent) Color(0xFF197AD7) else Color(0xFF333333)
    }

    Box(
        modifier = Modifier
            .fillMaxHeight()
            .weight(1f)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Text(text = tabText, fontSize = 42.sp, color = textColor)
    }
}

@Composable
fun MinePage() {
    val density = LocalDensity.current

    Box(modifier = Modifier.fillMaxSize()) {
        GlideImage(
            imageModel = { IMAGE_URL },
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(3840f / 2160),
        )

        var topBarHeight by remember {
            mutableStateOf(0.dp)
        }


        var collapsingHeight by remember {
            mutableStateOf(0.dp)
        }

        var collapsingOffset by remember {
            mutableStateOf(0.dp)
        }

        Column(
            Modifier
                .fillMaxWidth()
                .padding(top = topBarHeight)
                .offset(y = collapsingOffset)
                .background(Color.White)
                .onSizeChanged {
                    collapsingHeight = (it.height * density.density).dp
                }) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp), contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "内容1",
                    fontSize = 36.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF333333)
                )
            }
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .background(Color(0x99197ad7)), contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "内容2",
                    fontSize = 36.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF333333)
                )
            }
            Box(modifier = Modifier.fillMaxWidth()) {
                var showAll by remember {
                    mutableStateOf(false)
                }

                val maxLines by remember {
                    derivedStateOf { if (showAll) Int.MAX_VALUE else 1 }
                }

                val btnText by remember {
                    derivedStateOf { if (showAll) "隐藏" else "展开" }
                }

                Text(
                    text = "abcdefghijklmnopqrstuvwxyz1234567890abcdefghijklmnopqrstuvwxyz1234567890abcdefghijklmnopqrstuvwxyz1234567890",
                    maxLines = maxLines,
                    color = Color(0xff333333),
                    fontSize = 39.sp
                )

                val brush = remember {
                    Brush.horizontalGradient(listOf(Color(0x00000000), Color(0xff000000)))
                }

                Text(
                    text = btnText,
                    color = Color(0xffffffff),
                    fontSize = 39.sp,
                    modifier = Modifier
                        .background(brush)
                        .clickable { showAll = showAll.not() }
                        .padding(start = 32.dp)
                        .align(Alignment.BottomEnd)
                )
            }
        }

        val bgColor by remember {
            derivedStateOf {


                val alpha = if (topBarHeight.value == 0f) {
                    0f
                } else {
                    -(collapsingHeight + collapsingOffset - topBarHeight) / topBarHeight
                }.coerceIn(0f, 1f)

                Color.White.copy(alpha = alpha)
            }
        }

        Box(
            modifier = Modifier
                .background(bgColor)
                .onSizeChanged {
                    topBarHeight = (it.height * density.density).dp
                }
                .statusBarsPadding()
                .height(defaultTopBarHeight)
                .fillMaxWidth()
                .align(Alignment.TopCenter)
        ) {
            Box(
                modifier = Modifier
                    .height(80.dp)
                    .width(160.dp)
                    .background(Color(0xff197ad7))
                    .align(Alignment.CenterEnd)
            )
        }

        val bottomPadding by remember {
            derivedStateOf { topBarHeight + collapsingHeight + collapsingOffset }
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.TopCenter)
                .padding(top = bottomPadding)
        ) {
            val tabCount = 3

            val pagerState = rememberPagerState()
            val coroutineScope = rememberCoroutineScope()

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
                    .background(Color.Magenta)
            ) {
                repeat(tabCount) { index ->
                    Tab(tabText = "Tab${index + 1}", isCurrent = index == pagerState.targetPage) {
                        coroutineScope.launch { pagerState.animateScrollToPage(index) }
                    }
                }
            }

            val nestedScrollConnection = remember {
                object : NestedScrollConnection {
                    override fun onPreScroll(
                        available: Offset,
                        source: NestedScrollSource,
                    ): Offset {


                        val offsetY = (collapsingOffset.value + available.y)
                            .coerceIn(collapsingHeight.value * -1f, 0f)

                        val dy = offsetY - collapsingOffset.value

                        collapsingOffset = (offsetY / density.density).dp

                        return Offset(0f, dy)

//                        return available
                    }
                }
            }

            HorizontalPager(
                pageCount = tabCount,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .background(Color.Gray)
                    .nestedScroll(nestedScrollConnection), state = pagerState
            ) {
                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    items(50, key = { it }) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 16.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = "row $it", fontSize = 36.sp, color = Color.Magenta)
                        }
                    }
                }
            }
        }
    }

}

@Composable
fun OthersPage() {
    Column(modifier = Modifier.fillMaxSize()) {
        Box(
            modifier = Modifier
                .offset(y = (-300).dp)
                .fillMaxWidth()
                .aspectRatio(1f)
                .background(Color.Magenta.copy(0.5f))
        )
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .offset(y = (-300).dp)
                .weight(1f)
                .background(Color.Green)
        )
    }
}
