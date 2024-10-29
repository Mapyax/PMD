package com.example.pmu.jetpackcompose

import android.content.Context
import android.opengl.GLSurfaceView
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.pmu.data.NewsData
import com.example.pmu.openGL_ES.OpenGLRenderer

import com.example.pmu.viewmodel.NewsController

class JetPackComposeController {

    @Composable
    fun OpenGLView(context: Context) {
        AndroidView(factory = {
            GLSurfaceView(context).apply {
                setEGLContextClientVersion(2)
                //OpenGLSurfaceView(context)
                setRenderer(OpenGLRenderer(context))
                // Включите рендеринг
                renderMode = GLSurfaceView.RENDERMODE_CONTINUOUSLY
            }
        })
    }

    @Composable
    fun NewsScreen(newsViewModel: NewsController) {
        val newsItems by newsViewModel.newsItems.collectAsState()
        var showOpenGLView by remember { mutableStateOf(false) }

        Box(modifier = Modifier.fillMaxSize()) {
            if (showOpenGLView) {
                // Показ OpenGL View
                OpenGLView(context = LocalContext.current)
            } else {
                // Показ новостей
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight(0.95f)
                        .padding(10.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                    ) {
                        NewsCard(newsItems[0], Color.Yellow, Modifier.weight(1f), newsViewModel)
                        NewsCard(newsItems[1], Color.Red, Modifier.weight(1f), newsViewModel)
                    }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                    ) {
                        NewsCard(newsItems[2], Color.Cyan, Modifier.weight(1f), newsViewModel)
                        NewsCard(newsItems[3], Color.LightGray, Modifier.weight(1f), newsViewModel)
                    }
                }
            }

            // Кнопка внизу для переключения между OpenGL и новостями
            Button(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
                    .padding(10.dp),
                onClick = {
                    showOpenGLView = !showOpenGLView  // Переключение между экранами
                }
            ) {
                Text(text = if (showOpenGLView) "Вернуться к новостям" else "Показать OpenGL")
            }
        }
    }


    @Composable
    fun NewsCard(
        newsItem: NewsData,
        bgColor: Color,
        modifier: Modifier = Modifier,
        newsViewModel: NewsController
    ) {
        var hasLiked = newsViewModel.newsLikesState[newsItem.id] ?: false
        var hasDisliked = newsViewModel.newsDislikesState[newsItem.id] ?: false
        var cntViewLikes by remember { mutableLongStateOf(newsItem.cntLikes) }
        var cntViewDislikes by remember { mutableLongStateOf(newsItem.cntDislikes) }

        Card(
            modifier = modifier
                .fillMaxHeight()
                .padding(5.dp),
            shape = RoundedCornerShape(15.dp),
            elevation = CardDefaults.cardElevation(4.dp)
        ) {
            Box(
                modifier = Modifier
                    .background(bgColor)
                    .fillMaxSize()
            ) {
                Text(
                    text = newsItem.text.toString(),
                    color = Color.Black,
                    modifier = Modifier
                        .align(Alignment.Center)
                        .background(bgColor)
                        .fillMaxWidth()
                        .padding(10.dp),
                    textAlign = TextAlign.Center
                )
                Row(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .fillMaxWidth()
                        .background(Color.Black)
                        .padding(5.dp),
                    horizontalArrangement = Arrangement.SpaceAround,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(
                            onClick = {
                                if (!hasLiked) {
                                    newsViewModel.likeNews(newsItem.id)
                                    cntViewLikes++
                                    hasLiked = true

                                    if (hasDisliked) {
                                        newsViewModel.unDislikeNews(newsItem.id)
                                        cntViewDislikes--
                                        hasDisliked = false
                                    }
                                }
                            },
                        ) {
                            Icon(
                                imageVector = Icons.Default.KeyboardArrowUp,
                                contentDescription = "Like",
                                tint = if (hasLiked) Color.Green else Color.White
                            )
                        }
                        Text(
                            text = "+$cntViewLikes",
                            color = Color.Green,
                            fontSize = 16.sp
                        )
                    }
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(
                            onClick = {
                                if (!hasDisliked) {
                                    newsViewModel.dislikeNews(newsItem.id)
                                    cntViewDislikes++
                                    hasDisliked = true

                                    if (hasLiked) {
                                        newsViewModel.unLikeNews(newsItem.id)
                                        cntViewLikes--
                                        hasLiked = false
                                    }
                                }
                            },
                        ) {
                            Icon(
                                imageVector = Icons.Default.KeyboardArrowDown,
                                contentDescription = "Dislike",
                                tint = if (hasDisliked) Color.Red else Color.White
                            )
                        }
                        Text(
                            text = "-$cntViewDislikes",
                            color = Color.Red,
                            fontSize = 16.sp
                        )
                    }
                }
            }
        }
    }

}
