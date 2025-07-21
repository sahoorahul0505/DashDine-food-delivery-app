package com.kodebug.dashdine.ui.features.restaurant_detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kodebug.dashdine.data.DashDineApiService
import com.kodebug.dashdine.data.models.FoodItem
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
class RestaurantViewModel @Inject constructor(private val apiService: DashDineApiService) : ViewModel() {

    private val _uiState = MutableStateFlow<RestaurantState>(RestaurantState.Nothing)
    val uiState: StateFlow<RestaurantState> = _uiState.asStateFlow()

    private val _navigationEvent = MutableSharedFlow<RestaurantNavigationEvent>()
    val navigationEvent: SharedFlow<RestaurantNavigationEvent> = _navigationEvent.asSharedFlow()

    var error = ""
    var errorDescription = ""

    fun getFoodItemsForRestaurant(restaurantId: String) {
        viewModelScope.launch {
            _uiState.value = RestaurantState.Loading

            try {
            val response = safeApiCall {
                apiService.getFoodItemsForRestaurant(restaurantId)
            }
                when(response){
                    is ApiResponse.Success -> {
                        _uiState.value = RestaurantState.Success(response.data.foodItems)
                    }
                    else -> {
                        val errorResponse = (response as ApiResponse.Error).code
                        when(errorResponse){
                            400 -> {
                                error = "Bad Request"
                                errorDescription = "The request was invalid. Please check your parameters and try again."
                            }
                            401 -> {
                                error = "Unauthorized Access"
                                errorDescription = "You are not authorized to perform this action."
                            }
                            404 -> {
                                error = "Food Items Not Found"
                                errorDescription = "No food items were found for the selected Restaurant."
                            }
                            500 -> {
                                error = "Internal Server Error"
                                errorDescription = "Something went wrong on our end. Please try again later."
                            }
                            else -> {
                                error = "Unknown Error"
                                errorDescription = "An unexpected error occurred. Please check your internet connection."
                            }
                        }
                        _uiState.value = RestaurantState.Error
                        _navigationEvent.emit(RestaurantNavigationEvent.ShowErrorDialog)
                    }
                }
            }catch (e: Exception){
                _uiState.value = RestaurantState.Error
                _navigationEvent.emit(RestaurantNavigationEvent.ShowErrorDialog)
            }
        }
    }







    sealed class RestaurantNavigationEvent {
        data object NavigationToBack : RestaurantNavigationEvent()
        data object ShowErrorDialog : RestaurantNavigationEvent()
        data class NavigationToProductDetail(val productID: String) : RestaurantNavigationEvent()
    }

    sealed class RestaurantState {
        data object Nothing : RestaurantState()
        data object Loading : RestaurantState()
        data object Error : RestaurantState()
        data class Success(val data: List<FoodItem>) : RestaurantState()

    }
}