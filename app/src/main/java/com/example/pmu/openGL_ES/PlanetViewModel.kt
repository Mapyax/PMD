package com.example.pmu.openGL_ES

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import com.example.pmu.R

class PlanetViewModel : ViewModel() {
    val planets = mutableStateListOf(
        Planet("Солнце", "Солнце - звезда, вокруг которой вращаются все планеты Солнечной системы.", R.drawable.sun),
        Planet("Меркурий", "Меркурий - ближайшая к Солнцу планета.", R.drawable.mercuryplanet),
        Planet("Венера", "Венера - вторая планета от Солнца, часто называется 'сестрой Земли'.", R.drawable.venusplanet),
        Planet("Земля", "Земля - единственная планета, на которой известна жизнь.", R.drawable.earthplanet),
        Planet("Марс", "Марс - красная планета, известная своими солеными озерами.", R.drawable.marsplanet),
        Planet("Юпитер", "Юпитер - самая большая планета в Солнечной системе.", R.drawable.jupiterplanet),
        Planet("Сатурн", "Сатурн известен своими кольцами.", R.drawable.saturnplanet),
        Planet("Уран", "Уран - планета с необычным наклоном оси вращения.", R.drawable.uranplanet),
        Planet("Нептун", "Нептун - самая дальняя планета от Солнца.", R.drawable.netpunplanet),
    )
}