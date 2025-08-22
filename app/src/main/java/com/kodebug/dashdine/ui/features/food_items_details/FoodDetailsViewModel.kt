package com.kodebug.dashdine.ui.features.food_items_details

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kodebug.dashdine.data.DashDineApiService
import com.kodebug.dashdine.data.models.AddToCartRequest
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
class FoodDetailsViewModel @Inject constructor(val apiService: DashDineApiService) : ViewModel() {

    private val _uiState = MutableStateFlow<FoodDetailsUiState>(FoodDetailsUiState.Nothing)
    val uiState: StateFlow<FoodDetailsUiState> = _uiState.asStateFlow()

    private val _uiEvent = MutableSharedFlow<FoodDetailsEvents>()
    val uiEvent: SharedFlow<FoodDetailsEvents> = _uiEvent.asSharedFlow()

    private val _quantity = MutableStateFlow<Int>(1)
    val quantity: StateFlow<Int> = _quantity.asStateFlow()


    fun incrementQuantity() {
        if (_quantity.value == 10) {
            return
        }
        _quantity.value++
    }

    fun decrementQuantity() {
        if (_quantity.value == 1) {
            return
        }
        _quantity.value--
    }

    fun addToCart(restaurantId: String, foodItemId: String) {
        viewModelScope.launch {
            _uiState.value = FoodDetailsUiState.Loading
            val response = safeApiCall {
                apiService.addToCart(
                    AddToCartRequest(
                        restaurantId = restaurantId,
                        menuItemId = foodItemId,
                        quantity = _quantity.value
                    )
                )
            }

            when (response) {
                is ApiResponse.Success -> {
                    _uiState.value = FoodDetailsUiState.Success
                    _uiEvent.emit(FoodDetailsEvents.OnAddToCart)
                }

                is ApiResponse.Error -> {
//                    val errorResponse = (response as ApiResponse.Error).code
                    _uiState.value = FoodDetailsUiState.Error(response.message)
                    _uiEvent.emit(FoodDetailsEvents.ShowErrorDialog(response.message))
                }

                else -> {
                    _uiState.value = FoodDetailsUiState.Error("Unknown Error")
                    _uiEvent.emit(FoodDetailsEvents.ShowErrorDialog("Unknown Error"))
                }
            }
        }
    }

    fun goToCart() {
        viewModelScope.launch {
            _uiEvent.emit(FoodDetailsEvents.NavigationToCart)
        }
    }


    sealed class FoodDetailsUiState {
        object Nothing : FoodDetailsUiState()
        object Loading : FoodDetailsUiState()
        object Success : FoodDetailsUiState()
        data class Error(val message: String) : FoodDetailsUiState()
    }

    sealed class FoodDetailsEvents {
        data class ShowErrorDialog(val message: String) : FoodDetailsEvents()
        object OnAddToCart : FoodDetailsEvents()
        object NavigationToCart : FoodDetailsEvents()
    }
}