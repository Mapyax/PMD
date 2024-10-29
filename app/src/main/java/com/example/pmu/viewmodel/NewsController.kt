package com.example.pmu.viewmodel

import androidx.compose.runtime.mutableStateMapOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pmu.ui.data.NewsData
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlin.random.Random

class NewsController : ViewModel() {
    private val _newsItems = MutableStateFlow<Array<NewsData>>(emptyArray())
    val newsItems: StateFlow<Array<NewsData>> get() = _newsItems

    val newsLikesState = mutableStateMapOf<Long, Boolean>()
    val newsDislikesState = mutableStateMapOf<Long, Boolean>()

    private val newsList = arrayOf(
        NewsData(1, "Программисты начали использовать кофе в качестве переменной: теперь у них есть 'Coffee' вместо 'x'", Random.nextLong(100), Random.nextLong(100)),
        NewsData(2, "Сисадмины нашли баг в коде и решили, что это фича", Random.nextLong(100), Random.nextLong(100)),
        NewsData(3, "Группа разработчиков запустила новый фреймворк: ‘Почини-Все’", Random.nextLong(100), Random.nextLong(100)),
        NewsData(4, "Задача по алгоритмам на собеседовании превратилась в соревнование по отгадыванию мемов", Random.nextLong(100), Random.nextLong(100)),
        NewsData(5, "В команде разработчиков объявили ‘День Без Кода’ – все смотрят на экран и ничего не делают", Random.nextLong(100), Random.nextLong(100)),
        NewsData(6, "AI начал предсказывать не только ошибки, но и завтрашнюю погоду: теперь ошибки программирования всегда дождливые", Random.nextLong(100), Random.nextLong(100)),
        NewsData(7, "На конференции программистов ввели новую дисциплину: ‘Лучший Секретный Код’ – цель: написать код, который никто не поймет", Random.nextLong(100), Random.nextLong(100)),
        NewsData(8, "Разработчики осознали, что лучшее решение для всех багов – это игнорировать их до следующего релиза", Random.nextLong(100), Random.nextLong(100)),
        NewsData(9, "Программирование на выходных стало трендом: теперь код пишут даже в сне", Random.nextLong(100), Random.nextLong(100)),
        NewsData(10, "В сети появилось новое приложение для ‘программирования’ – оно делает только одно: засыпает за вас", Random.nextLong(100), Random.nextLong(100))
    )

    init {
        _newsItems.value = newsList.copyOfRange(0, 4)
        updateNews()
    }

    private fun updateNews() {
        viewModelScope.launch {
            while (true) {
                delay(5000)
                replaceRandomNews()
            }
        }
    }

    private fun replaceRandomNews() {
        val currentList = _newsItems.value.toMutableList()
        val randomIndex = Random.nextInt(0, currentList.size)
        val newNews = newsList[Random.nextInt(4, newsList.size)]


        val hasLiked = newsLikesState[newNews.id] ?: false
        val hasDisliked = newsDislikesState[newNews.id] ?: false

        val updatedNews = newNews.copy(
            cntLikes = if (hasLiked) newNews.cntLikes + 1 else newNews.cntLikes,
            cntDislikes = if (hasDisliked) newNews.cntDislikes + 1 else newNews.cntDislikes
        )


        currentList[randomIndex] = updatedNews
        _newsItems.value = currentList.toTypedArray()
    }


    fun likeNews(newsId: Long) {
        newsLikesState[newsId] = true
    }

    fun dislikeNews(newsId: Long) {
        newsDislikesState[newsId] = true
    }

    fun unLikeNews(newsId: Long) {
        newsLikesState[newsId] = false
    }

    fun unDislikeNews(newsId: Long) {
        newsDislikesState[newsId] = false
    }
}
