package com.kodebug.dashdine.ui.features.food_items_details

import android.widget.Toast
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.kodebug.dashdine.R
import com.kodebug.dashdine.components.BouncingDots
import com.kodebug.dashdine.components.ShadowButton
import com.kodebug.dashdine.data.models.FoodItem
import com.kodebug.dashdine.ui.DashDineErrorDialogBox
import com.kodebug.dashdine.ui.DashDineSuccessDialogBox
import com.kodebug.dashdine.ui.features.cart.CartScreen
import com.kodebug.dashdine.ui.features.restaurant_detail.RestaurantDetailContent
import com.kodebug.dashdine.ui.features.restaurant_detail.RestaurantDetailHeader
import com.kodebug.dashdine.ui.navigation.Cart
import com.kodebug.dashdine.ui.theme.Orange
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@OptIn(ExperimentalSharedTransitionApi::class, ExperimentalMaterial3Api::class)
@Composable
fun SharedTransitionScope.FoodDetailScreen(
    modifier: Modifier = Modifier,
    navController: NavController,
    foodItem: FoodItem,
    animatedVisibilityScope: AnimatedVisibilityScope,
    viewModel: FoodDetailsViewModel = hiltViewModel()
) {
    var count = viewModel.quantity.collectAsStateWithLifecycle()
    val uiState = viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val isLoading = remember { mutableStateOf(false) }
    val showSuccessDialog = remember { mutableStateOf(false) }
    val showErrorDialog = remember { mutableStateOf(false) }
    val successSheetState = rememberModalBottomSheetState()
    val errorSheetState = rememberModalBottomSheetState()
    val scope = rememberCoroutineScope()
    when (uiState.value) {
        is FoodDetailsViewModel.FoodDetailsUiState.Loading -> {
            isLoading.value = true
        }

        else -> {
            isLoading.value = false
        }
    }
    LaunchedEffect(Unit) {
        viewModel.uiEvent.collectLatest {
            when (it) {
                is FoodDetailsViewModel.FoodDetailsEvents.OnAddToCart -> {
                    scope.launch {
                        successSheetState.show()
                    }
                }

                is FoodDetailsViewModel.FoodDetailsEvents.NavigationToCart -> {
//                    navController.navigate("cart")
                    navController.navigate(Cart)
                }

                is FoodDetailsViewModel.FoodDetailsEvents.ShowErrorDialog -> {
                    scope.launch {
                        errorSheetState.show()
                    }
                }
            }
        }
    }
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        Column(modifier = modifier.fillMaxSize()) {
            RestaurantDetailHeader( // reused from restaurant detail screen
                imageUrl = foodItem.imageUrl,
                restaurantID = foodItem.id,
                onBackClick = { navController.popBackStack() },
                onFavoriteClick = {},
                animatedVisibilityScope = animatedVisibilityScope
            )
            RestaurantDetailContent(
                title = foodItem.name,
                description = foodItem.description,
                restaurantID = foodItem.id,
                animatedVisibilityScope = animatedVisibilityScope
            )
            Row(
                modifier = modifier
                    .padding(20.dp)
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "$${foodItem.price}",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = Orange,
                    modifier = modifier.sharedElement(
                        sharedContentState = rememberSharedContentState(key = "price/${foodItem.id}"),
                        animatedVisibilityScope = animatedVisibilityScope
                    )
                )

                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Count decrement
                    IconButton(
                        onClick = {
                            viewModel.decrementQuantity()
                        },
                        modifier = modifier.wrapContentSize(),
                        colors = IconButtonDefaults.iconButtonColors(
                            containerColor = Color.White,
                            contentColor = Orange
                        )
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.ic_minus),
                            contentDescription = null,
                            modifier = modifier.size(42.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("${count.value}", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                    Spacer(modifier = Modifier.width(6.dp))
                    // Count increment
                    IconButton(
                        onClick = {
                            viewModel.incrementQuantity()
                        },
                        modifier = modifier.wrapContentSize(),
                        colors = IconButtonDefaults.iconButtonColors(
                            containerColor = Orange,
                            contentColor = Color.White
                        )
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.ic_add),
                            contentDescription = null,
                            modifier = modifier.size(42.dp)
                        )
                    }
                }
            }
        }

        // Add to cart button
        ShadowButton(
            onClick = {
                viewModel.addToCart(
                    restaurantId = foodItem.restaurantId,
                    foodItemId = foodItem.id
                )
            },
            enabled = !isLoading.value,
            modifier = modifier
                .padding(bottom = 50.dp)
                .fillMaxWidth(.48f)
                .height(64.dp)
                .align(alignment = Alignment.BottomCenter),
            shape = RoundedCornerShape(32.dp),
            containerColor = Orange,
            contentPadding = PaddingValues(vertical = 6.dp, horizontal = 8.dp),
            shadowColor = Color.Gray.copy(alpha = .3f)
        ) {
            AnimatedContent(
                targetState = isLoading.value,
                transitionSpec = {
//                            (slideInHorizontally { it } + fadeIn(animationSpec = tween(600))) togetherWith
//                                    (slideOutHorizontally { -it } + fadeOut(animationSpec = tween(600)))
                    fadeIn(animationSpec = tween(300)) + scaleIn(initialScale = 0.8f) togetherWith
                            fadeOut(animationSpec = tween(300)) + scaleOut(targetScale = 0.8f)
                },
                contentAlignment = Alignment.Center
            ) { target ->
                if (target) {
                    BouncingDots(
                        color = Color.White,
                        dotSize = 8.dp,
                        spaceBetween = 10.dp,
                        travelDistance = 6.dp
                    )
//                            LinearProgressIndicator(modifier = modifier.fillMaxHeight(), trackColor = Orange, color = Color.White)
                } else {
                    Row(
                        modifier = modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        IconButton(
                            onClick = {
                                viewModel.goToCart()
                            },
                            modifier = modifier.size(48.dp),
                            colors = IconButtonDefaults.iconButtonColors(containerColor = Color.White)
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_shopping_bag),
                                contentDescription = null,
                                tint = Color.Unspecified,
                                modifier = modifier.size(30.dp)
                            )
                        }
                        Spacer(modifier = modifier.width(16.dp))
                        Box(modifier = modifier.weight(1f), contentAlignment = Alignment.Center) {
                            Text(
                                text = "Add to Cart",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color.White
                            )
                        }

                    }
                }
            }
        }
    }

    if (successSheetState.isVisible) {
        ModalBottomSheet(onDismissRequest = {
            scope.launch {
                successSheetState.hide()
            }
        }, sheetState = successSheetState, containerColor = Color.White, tonalElevation = 10.dp) {
            DashDineSuccessDialogBox(
                title = "Successfully added to cart",
                description = "Item has been added to your cart",
                onDismiss = {
                    viewModel.goToCart()
                    scope.launch {
                        successSheetState.hide()
//                        showDialog = false
                    }
                }
            )
        }
    }

    if (errorSheetState.isVisible) {
        ModalBottomSheet(onDismissRequest = {
            scope.launch {
                errorSheetState.hide()
            }
        }, sheetState = errorSheetState, containerColor = Color.White, tonalElevation = 10.dp) {
            DashDineErrorDialogBox(
                title = "Failed to add to cart",
                description = "Item could not be added to your cart",
                onDismiss = {
                    scope.launch {
                        errorSheetState.hide()
//                        showDialog = false
                    }
                }
            )
        }
    }
}