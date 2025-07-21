package com.kodebug.dashdine.ui.features.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kodebug.dashdine.data.DashDineApiService
import com.kodebug.dashdine.data.models.Category
import com.kodebug.dashdine.data.models.Restaurant
import com.kodebug.dashdine.data.remote.ApiResponse
import com.kodebug.dashdine.data.remote.safeApiCall
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(private val apiService: DashDineApiService) : ViewModel() {

    private val _uiState = MutableStateFlow<HomeScreenState>(HomeScreenState.Loading)
    val uiState: StateFlow<HomeScreenState> = _uiState.asStateFlow()

    private val _navigationEvent = MutableSharedFlow<HomeScreenNavigationEvent>()
    val navigationEvent: SharedFlow<HomeScreenNavigationEvent> = _navigationEvent.asSharedFlow()

    var categories = emptyList<Category>()
    var restaurants = emptyList<Restaurant>()

    init {
        viewModelScope.launch {
            categories = getAllCategories()
            restaurants = getPopularRestaurants()

            if (categories.isNotEmpty() || restaurants.isNotEmpty()) {
                _uiState.value = HomeScreenState.Success
            } else {
                _uiState.value = HomeScreenState.Error
            }
        }
    }

    private suspend fun getAllCategories(): List<Category> {
        _uiState.value = HomeScreenState.Loading
        var categoryList = emptyList<Category>()
        val response = safeApiCall {
            apiService.getAllCategories()
        }
        when (response) {
            is ApiResponse.Success -> {
                categoryList = response.data.data
//                    _uiState.value = HomeScreenState.Success
            }

            else -> {

            }
        }
        return categoryList
    }

    private suspend fun getPopularRestaurants(): List<Restaurant> {
        _uiState.value = HomeScreenState.Loading
        var restaurantList = emptyList<Restaurant>()
        val response = safeApiCall {
            apiService.getRestaurants(latitude = 40.7128, longitude = -74.0060)
//            apiService.getRestaurants(latitude =  20.3535028, longitude = 85.8219226)
        }
        when (response) {
            is ApiResponse.Success -> {
                restaurantList = response.data.data
//                _uiState.value = HomeScreenState.Success
            }

            else -> {

            }
        }
        return restaurantList
    }

    fun navigateToRestaurantDetail(it: Restaurant) {
        viewModelScope.launch {
            _navigationEvent.emit(HomeScreenNavigationEvent.NavigationToDetail(it.id, it.name, it.imageUrl))
        }
    }

    sealed class HomeScreenState {
        object Loading : HomeScreenState()
        object Error : HomeScreenState()
        object Success : HomeScreenState()
    }

    sealed class HomeScreenNavigationEvent {
        data class NavigationToDetail(
            val restaurantID: String,
            val restaurantName: String,
            val restaurantImageUrl: String
        ) : HomeScreenNavigationEvent()
    }
}