package me.muhaimin.mandirinews.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import me.muhaimin.mandirinews.data.entity.Country
import me.muhaimin.mandirinews.data.remote.response.Article
import me.muhaimin.mandirinews.data.remote.retrofit.ApiConfig

class HomeViewModel : ViewModel() {
    private val _pageHeadlineNews = MutableLiveData<Int>().apply { value = 1 }
    private val _listHeadlineNews = MutableLiveData<ArrayList<Article>>()
    val listHeadlineNews: LiveData<ArrayList<Article>> = _listHeadlineNews

    private val _pageEverythingNews = MutableLiveData<Int>().apply { value = 1 }
    private val _listEverythingNews = MutableLiveData<ArrayList<Article>>()
    val listEverythingNews: LiveData<ArrayList<Article>> = _listEverythingNews

    private val _topHeadlineNews = MutableLiveData<Article>()
    val topHeadlineNews: LiveData<Article> = _topHeadlineNews

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> = _errorMessage

    private fun getHeadlineNews(country: Country = Country.UNITED_STATES) {
        viewModelScope.launch {
            try {
                if (_pageHeadlineNews.value == 1) {
                    _isLoading.postValue(true)
                }

                val response = ApiConfig.getApiService().getHeadlineNews(
                    country = country.code,
                    page = _pageHeadlineNews.value!!,
                    pageSize = 20
                )

                // top headline news
                if (_pageHeadlineNews.value == 1) {
                    _topHeadlineNews.value = response.articles?.get(0)
                    response.articles?.removeAt(0)
                }

                _isLoading.postValue(false)
                val currentList = _listHeadlineNews.value ?: ArrayList()
                currentList.addAll(response.articles!!)
                _listHeadlineNews.postValue(currentList)

            } catch (e: Exception) {
                _isLoading.postValue(false)
                _errorMessage.postValue("Failed to fetch data")
            }
        }
    }

    private fun getEverythingNews(query: String = "a") {
        viewModelScope.launch {
            try {
                if (_pageEverythingNews.value == 1) {
                    _isLoading.postValue(false)
                }

                val response = ApiConfig.getApiService().getEverythingNews(
                    query = query,
                    page = _pageEverythingNews.value!!,
                    pageSize = 20
                )

                _isLoading.postValue(false)
                val currentList = _listEverythingNews.value ?: ArrayList()
                currentList.addAll(response.articles!!)
                _listEverythingNews.postValue(currentList)

            } catch (e: Exception) {
                _isLoading.postValue(false)
                _errorMessage.postValue("Failed to fetch data")
            }
        }
    }

    fun increasePageHeadlineNews() {
        _pageHeadlineNews.value = _pageHeadlineNews.value?.plus(1)
        getHeadlineNews()
    }

    fun increasePageEverythingNews() {
        _pageEverythingNews.value = _pageEverythingNews.value?.plus(1)
        getEverythingNews()
    }

    fun getNews() {
        getHeadlineNews()
        getEverythingNews()
    }
}