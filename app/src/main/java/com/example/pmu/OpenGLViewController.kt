package com.example.pmu

import androidx.compose.ui.Alignment
import android.opengl.GLSurfaceView
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.pmu.openGL_ES.GL
import com.example.pmu.openGL_ES.MoonRenderer
import com.example.pmu.openGL_ES.Planet
import com.example.pmu.openGL_ES.PlanetViewModel
import com.example.pmu.openGL_ES.Water

class OpenGLViewController {

    @Composable
    fun MainView(planetModel: PlanetViewModel = viewModel()) {
        var currentScreen by remember { mutableStateOf(Screen.OpenGL) }

        Box(modifier = Modifier.fillMaxSize()) {
            when (currentScreen) {
                Screen.OpenGL -> {
                    OpenGLView(currentScreen = currentScreen, onScreenChange = { screen ->
                        currentScreen = screen
                    })
                }
//                Screen.Advertisement -> {
//                    AdvertisementWindow(currentScreen = currentScreen, onScreenChange = { screen ->
//                        currentScreen = screen
//                    })
//                }
                Screen.Moon -> {
                    MoonInfoScreen(onBack = {
                        currentScreen = Screen.OpenGL
                    })
                }
                Screen.PlanetInfo0 -> {
                    val planet = planetModel.planets[0]
                    PlanetInfoScreen(planet = planet, onBack = {
                        currentScreen = Screen.OpenGL
                    })
                }
                Screen.PlanetInfo1 -> {
                    val planet = planetModel.planets[1]
                    PlanetInfoScreen(planet = planet, onBack = {
                        currentScreen = Screen.OpenGL
                    })
                }
                Screen.PlanetInfo2 -> {
                    val planet = planetModel.planets[2]
                    PlanetInfoScreen(planet = planet, onBack = {
                        currentScreen = Screen.OpenGL
                    })
                }
                Screen.PlanetInfo3 -> {
                    val planet = planetModel.planets[3]
                    PlanetInfoScreen(planet = planet, onBack = {
                        currentScreen = Screen.OpenGL
                    })
                }
                Screen.PlanetInfo4 -> {
                    val planet = planetModel.planets[4]
                    PlanetInfoScreen(planet = planet, onBack = {
                        currentScreen = Screen.OpenGL
                    })
                }
                Screen.PlanetInfo5 -> {
                    val planet = planetModel.planets[5]
                    PlanetInfoScreen(planet = planet, onBack = {
                        currentScreen = Screen.OpenGL
                    })
                }
                Screen.PlanetInfo6 -> {
                    val planet = planetModel.planets[6]
                    PlanetInfoScreen(planet = planet, onBack = {
                        currentScreen = Screen.OpenGL
                    })
                }
                Screen.PlanetInfo7 -> {
                    val planet = planetModel.planets[7]
                    PlanetInfoScreen(planet = planet, onBack = {
                        currentScreen = Screen.OpenGL
                    })
                }
                Screen.PlanetInfo8 -> {
                    NeptuneInfoScreen(onBack = {
                        currentScreen = Screen.OpenGL
                    })
                }

                Screen.Advertisement -> TODO()
            }
        }
    }

    @Composable
    fun OpenGLView(currentScreen: Screen, onScreenChange: (Screen) -> Unit) {
        val context = LocalContext.current
        val renderer = remember { GL(context) }

        Box(modifier = Modifier.fillMaxSize()) {
            AndroidView(factory = {
                GLSurfaceView(context).apply {
                    setEGLContextClientVersion(2)
                    setRenderer(renderer)
                    renderMode = GLSurfaceView.RENDERMODE_CONTINUOUSLY
                }
            }, modifier = Modifier.fillMaxSize())

            // Row moved to the top
            Row(
                modifier = Modifier
                    .align(Alignment.TopCenter)  // Align to the top
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Button(
                    onClick = {
                        var temp = renderer.selectedPlanet - 1
                        if (temp < 0) temp = renderer.planetsSize() - 1
                        renderer.selectedPlanet = temp % renderer.planetsSize()
                    },
                    modifier = Modifier.weight(0.5f)
                ) {
                    Text("◀")
                }

                Spacer(modifier = Modifier.width(8.dp))

                Button(
                    onClick = { when (renderer.selectedPlanet) {
                        0 -> {onScreenChange(Screen.PlanetInfo0)}
                        1 -> {onScreenChange(Screen.PlanetInfo1)}
                        2 -> {onScreenChange(Screen.PlanetInfo2)}
                        3 -> {onScreenChange(Screen.PlanetInfo3)}
                        4 -> {onScreenChange(Screen.Moon)}
                        5 -> {onScreenChange(Screen.PlanetInfo4)}
                        6 -> {onScreenChange(Screen.PlanetInfo5)}
                        7 -> {onScreenChange(Screen.PlanetInfo6)}
                        8 -> {onScreenChange(Screen.PlanetInfo7)}
                        9 -> {onScreenChange(Screen.PlanetInfo8)}
                        else -> {}
                    }},
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Информация")
                }

                Spacer(modifier = Modifier.width(8.dp))

                Button(
                    onClick = { renderer.selectedPlanet = (renderer.selectedPlanet + 1) % renderer.planetsSize() },
                    modifier = Modifier.weight(0.5f)
                ) {
                    Text("▶")
                }
            }
        }
    }


    @Composable
    fun MoonInfoScreen(onBack: () -> Unit) {
        val context = LocalContext.current
        val moonRenderer = remember { MoonRenderer(context) }

        Box(modifier = Modifier.fillMaxSize()) {
            AndroidView(factory = {
                GLSurfaceView(context).apply {
                    setEGLContextClientVersion(2)
                    setRenderer(moonRenderer)
                    renderMode = GLSurfaceView.RENDERMODE_CONTINUOUSLY
                }
            }, modifier = Modifier.fillMaxSize())

            Button(
                onClick = { onBack() },
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(16.dp)
            ) {
                Text("Назад")
            }

            Column(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(16.dp)
                    .background(Color(0x80000000)) // Полупрозрачный фон для лучшей читабельности
                    .padding(16.dp)
            ) {
                Text(
                    text = "Информация о Луне",
                    style = TextStyle(
                        fontSize = 20.sp, // Размер шрифта
                        fontWeight = FontWeight.Bold, // Полужирный шрифт
                        color = Color.White
                    )
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Луна — единственный естественный спутник Земли и пятый по величине спутник в Солнечной системе.",
                    color = Color.White
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Она находится на среднем расстоянии 384 400 км от Земли и имеет диаметр примерно 3 474 км.",
                    color = Color.White
                )
            }
        }
    }

    @Composable
    fun NeptuneInfoScreen(onBack: () -> Unit) {
        val context = LocalContext.current
        val neptuneRenderer = remember { Water(context) } // Обновите на ваш рендерер для Нептуна

        Box(modifier = Modifier.fillMaxSize()) {
            AndroidView(factory = {
                GLSurfaceView(context).apply {
                    setEGLContextClientVersion(2)
                    setRenderer(neptuneRenderer) // Используйте рендерер для Нептуна
                    renderMode = GLSurfaceView.RENDERMODE_CONTINUOUSLY
                }
            }, modifier = Modifier.fillMaxSize())

            Button(
                onClick = { onBack() },
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(16.dp)
            ) {
                Text("Назад")
            }

            Column(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(16.dp)
                    .background(Color(0x80000000)) // Полупрозрачный фон для лучшей читабельности
                    .padding(16.dp)
            ) {
                Text(
                    text = "Информация о Нептуне",
                    style = TextStyle(
                        fontSize = 20.sp, // Размер шрифта
                        fontWeight = FontWeight.Bold, // Полужирный шрифт
                        color = Color.White
                    )
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Нептун — восьмая планета от Солнца и последняя в Солнечной системе.",
                    color = Color.White
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Она находится на среднем расстоянии около 4,5 миллиардов километров от Солнца.",
                    color = Color.White
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Нептун — газовый гигант, его атмосфера состоит в основном из водорода, гелия и метана.",
                    color = Color.White
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Температура в атмосфере Нептуна достигает -214 °C.",
                    color = Color.White
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Нептун имеет 14 известных спутников, самым большим из которых является Тритон.",
                    color = Color.White
                )
            }
        }
    }

    @Composable
    fun PlanetInfoScreen(planet: Planet, onBack: () -> Unit) {
        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
                    .background(Color(0x80000000))
            ) {
                Text(
                    text = planet.name,
                    style = TextStyle(
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                )

                Spacer(modifier = Modifier.height(8.dp))

                Image(
                    painter = painterResource(id = planet.imageResId),
                    contentDescription = planet.name,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .padding(bottom = 16.dp)
                )

                Text(
                    text = planet.description,
                    color = Color.White
                )
            }

            Button(
                onClick = { onBack() },
                modifier = Modifier.run {
                    align(Alignment.TopCenter)
                                .padding(16.dp)
                }
            ) {
                Text("Назад")
            }
        }
    }

    enum class Screen() {
        OpenGL,
        Advertisement,
        Moon,
        PlanetInfo0,
        PlanetInfo1,
        PlanetInfo2,
        PlanetInfo3,
        PlanetInfo4,
        PlanetInfo5,
        PlanetInfo6,
        PlanetInfo7,
        PlanetInfo8,
    }
}