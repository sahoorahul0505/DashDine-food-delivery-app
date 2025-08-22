package com.kodebug.dashdine.ui.features.cart

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kodebug.dashdine.data.DashDineApiService
import com.kodebug.dashdine.data.models.CartItem
import com.kodebug.dashdine.data.models.CartResponse
import com.kodebug.dashdine.data.models.UpdateCartItemRequest
import com.kodebug.dashdine.data.remote.ApiResponse
import com.kodebug.dashdine.data.remote.safeApiCall
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class CartViewModel @Inject constructor(val apiService: DashDineApiService) : ViewModel() {

    private val _uiState = MutableStateFlow<CartUiState>(CartUiState.Nothing)
    val uiState: StateFlow<CartUiState> = _uiState

    private val _uiEvent = MutableSharedFlow<CartEvent>()
    val uiEvent: MutableSharedFlow<CartEvent> = _uiEvent

    private var cartResponse: CartResponse? = null
    var errorTitle = ""
    var errorMessage = ""

    init {
        getCart()
    }

    fun getCart() {
        viewModelScope.launch {
            _uiState.value = CartUiState.Loading
//            delay(600)
            val response = safeApiCall {
                apiService.getCart()
            }
            when (response) {
                is ApiResponse.Success -> {
                    cartResponse = response.data
                    _uiState.value = CartUiState.Success(response.data)
                }

                is ApiResponse.Error -> {
                    _uiState.value = CartUiState.Error(response.message)
                }

                else -> {
                    _uiState.value =
                        CartUiState.Error("Something went wrong \n Please check your internet connection")
                }
            }
        }
    }

    fun incrementQuantity(cartItem: CartItem) {
        if (cartItem.quantity == 10) {
            return
        }
        updateCartItemQuantity(cartItem, cartItem.quantity + 1)
    }

    fun decrementQuantity(cartItem: CartItem) {
        if (cartItem.quantity == 1) {
            return
        }
        updateCartItemQuantity(cartItem, cartItem.quantity - 1)
    }

    fun updateCartItemQuantity(cartItem: CartItem, quantity: Int) {
        viewModelScope.launch {
//            _uiState.value = CartUiState.Loading
            val response = safeApiCall {
                apiService.updateCart(
                    UpdateCartItemRequest(
                        cartItemId = cartItem.id,
                        quantity = quantity
                    )
                )
            }
            when (response) {
                is ApiResponse.Success -> {
                    getCart()
                }

                is ApiResponse.Error -> {
                    cartResponse?.let {
                        _uiState.value = CartUiState.Success(cartResponse!!)
                    }
                    errorTitle = "Failed to update cart"
                    errorMessage = "An error occurred while updating the cart"
                    _uiEvent.emit(CartEvent.OnUpdateQuantityError(response.message))
                }

                else -> {
                    cartResponse?.let {
                        _uiState.value = CartUiState.Success(cartResponse!!)
                    }
                    _uiEvent.emit(CartEvent.OnUpdateQuantityError("Something went wrong"))
                }
            }
        }
    }

    fun removeItemFromCart(cartItem: CartItem) {
        viewModelScope.launch {
            _uiState.value = CartUiState.Loading
            val response = safeApiCall {
                apiService.deleteCartItem(
                    cartItem.id
                )
            }
            when (response) {
                is ApiResponse.Success -> {
                    getCart()
                }

                is ApiResponse.Error -> {
                    cartResponse?.let {
                        _uiState.value = CartUiState.Success(cartResponse!!)
                    }
                    errorTitle = "Failed to remove item"
                    errorMessage = "An error occurred while removing the item"
                    _uiEvent.emit(CartEvent.OnRemoveItemError(response.message))
                }

                else -> {
                    cartResponse?.let {
                        _uiState.value = CartUiState.Success(cartResponse!!)
                    }
                    _uiEvent.emit(CartEvent.OnRemoveItemError("Something went wrong"))
                }
            }
        }
    }

    fun checkout() {

    }

    sealed class CartUiState {
        object Nothing : CartUiState()
        object Loading : CartUiState()
        data class Success(val cart: CartResponse) : CartUiState()
        data class Error(val message: String) : CartUiState()
    }

    sealed class CartEvent {
        data class ShowErrorDialog(val message: String) : CartEvent()
        data class OnUpdateQuantityError(val message: String) : CartEvent()
        data class OnRemoveItemError(val message: String) : CartEvent()
        object OnCheckoutNavigation : CartEvent()
    }
}