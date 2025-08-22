package com.kodebug.dashdine.ui.features.cart

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.PullToRefreshDefaults
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.focusModifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import coil3.compose.AsyncImage
import com.kodebug.dashdine.R
import com.kodebug.dashdine.components.ItemCardLarge
import com.kodebug.dashdine.components.ShadowButton
import com.kodebug.dashdine.data.models.CartItem
import com.kodebug.dashdine.data.models.CheckoutDetails
import com.kodebug.dashdine.data.models.FoodItem
import com.kodebug.dashdine.ui.DashDineErrorDialogBox
import com.kodebug.dashdine.ui.navigation.FoodDetail
import com.kodebug.dashdine.ui.theme.Orange
import com.kodebug.dashdine.utils.StringUtils
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CartScreen(
    modifier: Modifier = Modifier,
    navController: NavController,
    viewModel: CartViewModel = hiltViewModel()
) {

    val uiState = viewModel.uiState.collectAsStateWithLifecycle()

    val errorTitle = viewModel.errorTitle
    val errorMessage = viewModel.errorMessage
    val isRefreshing = (uiState.value is CartViewModel.CartUiState.Loading)
    val pullRefreshState = rememberPullToRefreshState()
    val showErrorDialog = rememberModalBottomSheetState()
    val removeBottomSheetState = rememberModalBottomSheetState()
    val updateBottomSheetState = rememberModalBottomSheetState()
    val scope = rememberCoroutineScope()

    LaunchedEffect(key1 = true) {
        viewModel.uiEvent.collectLatest { even ->
            when (even) {
                is CartViewModel.CartEvent.OnRemoveItemError -> {
                    scope.launch {
//                        removeBottomSheetState.show()
                        showErrorDialog.show()
                    }
                }

                is CartViewModel.CartEvent.OnUpdateQuantityError -> {
                    scope.launch {
//                        updateBottomSheetState.show()
                        showErrorDialog.show()
                    }
                }

                CartViewModel.CartEvent.OnCheckoutNavigation -> {

                }

                else -> {
                    CartViewModel.CartUiState.Nothing
                }
            }
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        Column(
            modifier = modifier
                .padding(top = 50.dp)
                .fillMaxSize()
        ) {
            CartHeader(onBackClick = { navController.popBackStack() })
            PullToRefreshBox(
                modifier = modifier.fillMaxSize(),
                isRefreshing = isRefreshing,
                state = pullRefreshState,
                onRefresh = {
                    viewModel.getCart()
                },
                indicator = {
                    if (pullRefreshState.distanceFraction > 0f && !isRefreshing) {
                        PullToRefreshDefaults.Indicator(
                            state = pullRefreshState,
                            isRefreshing = false, // this avoids spinner during refresh
                            modifier = Modifier.align(Alignment.TopCenter),
                            containerColor = Color.White,
                            color = Orange
                        )
                    }
                }
            ) {
                when (uiState.value) {
                    is CartViewModel.CartUiState.Error -> {
                        val errorMessage = (uiState.value as CartViewModel.CartUiState.Error).message

                        Column(
                            modifier = modifier
                                .fillMaxSize()
                                .verticalScroll(rememberScrollState()),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = errorMessage,
                                fontSize = 16.sp,
                                textAlign = TextAlign.Center,
                                color = Color.Red
                            )
                            Spacer(modifier = modifier.height(50.dp))
                            Text(text = "Pull down to refresh", fontSize = 18.sp, color = Color.Gray)
                        }

                    }

                    is CartViewModel.CartUiState.Loading -> {
                        LinearProgressIndicator(
                            modifier = modifier
                                .padding(top = 6.dp)
                                .fillMaxWidth()
                                .height(2.dp),
                            trackColor = Orange.copy(alpha = .4f),
                            color = Orange
                        )
                    }

                    is CartViewModel.CartUiState.Success -> {
                        val data = (uiState.value as CartViewModel.CartUiState.Success).cart

                        LazyColumn(modifier = modifier.fillMaxWidth()) {

                            items(data.items) {
                                CartItemCard(
                                    cartItem = it,
                                    onItemClicked = {
//                                        navController.navigate(FoodDetail(foodItem = it.menuItemId))
                                    },
                                    onRemoveClicked = {
                                        viewModel.removeItemFromCart(it)
                                    },
                                    onIncrementClicked = { cartItem ->
                                        viewModel.incrementQuantity(cartItem)
                                    },
                                    onDecrementClicked = { cartItem ->
                                        viewModel.decrementQuantity(cartItem)
                                    }
                                )
                            }

                            item {
                                CheckoutBillSection(data.checkoutDetails)
                                Spacer(modifier = modifier.height(200.dp))
                            }
                        }
                    }

                    else -> {
                        CartViewModel.CartUiState.Nothing
                    }
                }
            }
        }
        if (uiState.value is CartViewModel.CartUiState.Success) {
            CheckoutButton(
                onCheckoutButtonClick = {
                    viewModel.checkout()
                },
                modifier = modifier.align(alignment = Alignment.BottomCenter)
            )
        }
    }

    if (showErrorDialog.isVisible) {
        ModalBottomSheet(onDismissRequest = {
            scope.launch {
                showErrorDialog.hide()
            }
        }, sheetState = showErrorDialog, containerColor = Color.White, tonalElevation = 10.dp) {
            DashDineErrorDialogBox(
                title = errorTitle,
                description = errorMessage,
                onDismiss = {
                    scope.launch {
                        showErrorDialog.hide()
                    }
                }
            )
        }
    }

//    if (removeBottomSheetState.isVisible){
//        ModalBottomSheet(onDismissRequest = {
//            scope.launch {
//                removeBottomSheetState.hide()
//            }
//        }, sheetState = removeBottomSheetState, containerColor = Color.White, tonalElevation = 10.dp) {
//            DashDineErrorDialogBox(
//                title = errorTitle,
//                description = errorMessage,
//                onDismiss = {
//                    scope.launch {
//                        removeBottomSheetState.hide()
//                    }
//                }
//            )
//        }
//    }
//
//    if (updateBottomSheetState.isVisible){
//        ModalBottomSheet(onDismissRequest = {
//            scope.launch {
//                updateBottomSheetState.hide()
//            }
//        }, sheetState = updateBottomSheetState, containerColor = Color.White, tonalElevation = 10.dp) {
//            DashDineErrorDialogBox(
//                title = errorTitle,
//                description = errorMessage,
//                onDismiss = {
//                    scope.launch {
//                        updateBottomSheetState.hide()
//                    }
//                }
//            )
//        }
//    }
}

@Composable
fun CartHeader(onBackClick: () -> Unit) {
    Box(
        modifier = Modifier
            .padding(bottom = 20.dp)
            .fillMaxWidth()
    ) {
        FilledIconButton(
            onClick = onBackClick,
            colors = IconButtonDefaults.iconButtonColors(
                containerColor = Color.White,
                contentColor = Color.Black
            ),
            shape = RoundedCornerShape(14.dp),
            modifier = Modifier
                .padding(top = 18.dp, start = 20.dp)
                .size(40.dp)
                .shadow(
                    elevation = 20.dp,
                    shape = RoundedCornerShape(14.dp),
                    spotColor = Color.Gray,
                    ambientColor = Color.Gray
                )
                .align(alignment = Alignment.TopStart)
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_back_left),
                contentDescription = "back button",
                modifier = Modifier.size(14.dp)
            )
        }
        Text(
            text = "Cart",
            fontSize = 20.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color.Black,
            modifier = Modifier.align(alignment = Alignment.Center)
        )
    }
}


@Composable
fun CartItemCard(
    cartItem: CartItem,
    onItemClicked: (CartItem) -> Unit,
    onRemoveClicked: (CartItem) -> Unit,
    onIncrementClicked: (CartItem) -> Unit,
    onDecrementClicked: (CartItem) -> Unit
) {
    Box(
        modifier = Modifier
            .padding(horizontal = 20.dp, vertical = 24.dp)
            .fillMaxWidth()
            .height(100.dp)
    ) {
        Row(modifier = Modifier.fillMaxSize()) {
            ItemCardLarge(
                item = cartItem,
                onClick = {
                    onItemClicked.invoke(cartItem)
                },
                shape = RoundedCornerShape(24.dp),
                contentPadding = PaddingValues(0.dp),
                shadowColor = Color.Yellow.copy(alpha = .15f),
                shadowCornerRadius = 24.dp
            ) {
                AsyncImage(
                    model = cartItem.menuItemId.imageUrl,
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .width(100.dp)
                        .height(100.dp)
                        .clip(RoundedCornerShape(24.dp)),
                )
//                Image(
//                    painter = painterResource(id = R.drawable.welcome_background),
//                    contentDescription = null,
//                    contentScale = ContentScale.Crop,
//                    modifier = Modifier
//                        .width(100.dp)
//                        .height(100.dp),
//                )
            }
            Column(
                modifier = Modifier
                    .padding(start = 18.dp)
                    .fillMaxSize(),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = cartItem.menuItemId.name, fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black,
                    )
                    IconButton(
                        onClick = {
                            onRemoveClicked.invoke(cartItem)
                        },
                        modifier = Modifier.size(34.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "remove item",
                            modifier = Modifier.size(18.dp),
                            tint = Orange
                        )
                    }
                }
                Text(
                    text = cartItem.menuItemId.description,
                    overflow = TextOverflow.Ellipsis,
                    fontSize = 14.sp,
                    color = Color.Gray,
                    maxLines = 1,
                    modifier = Modifier.padding(end = 30.dp)
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = cartItem.menuItemId.price.toString(),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Orange
                    )
                    // Count decrement
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(
                            onClick = {
                                onDecrementClicked.invoke(cartItem)
                            },
                            modifier = Modifier.size(26.dp),
                            colors = IconButtonDefaults.iconButtonColors(
                                containerColor = Color.White,
                                contentColor = Orange
                            )
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.ic_minus),
                                contentDescription = null,
                                modifier = Modifier.size(26.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(7.dp))
                        Text(
                            cartItem.quantity.toString(),
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        // Count increment
                        IconButton(
                            onClick = {
                                onIncrementClicked.invoke(cartItem)
                            },
                            modifier = Modifier.size(26.dp),
                            colors = IconButtonDefaults.iconButtonColors(
                                containerColor = Orange,
                                contentColor = Color.White
                            )
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.ic_add),
                                contentDescription = null,
                                modifier = Modifier.size(30.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@SuppressLint("DefaultLocale")
@Composable
fun CheckoutBillSection(checkoutDetails: CheckoutDetails) {
    Box(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier
                .padding(horizontal = 20.dp, vertical = 20.dp)
                .fillMaxWidth()
        ) {
            CheckoutRowItem(
                title = "Subtotal",
                value = checkoutDetails.subTotal,
                currency = "USD"
            )
            HorizontalDivider(
                modifier = Modifier.padding(vertical = 12.dp),
                color = Color.LightGray.copy(alpha = .4f)
            )
            CheckoutRowItem(
                title = "Tax and Fees",
                value = checkoutDetails.tax,
                currency = "USD"
            )
            HorizontalDivider(
                modifier = Modifier.padding(vertical = 12.dp),
                color = Color.LightGray.copy(alpha = .4f)
            )
            CheckoutRowItem(
                title = "Delivery Fee",
                value = checkoutDetails.deliveryFee,
                currency = "USD"
            )
            HorizontalDivider(
                modifier = Modifier.padding(vertical = 12.dp),
                color = Color.LightGray.copy(alpha = .4f)
            )
            CheckoutRowItem(
                title = "Total Amount",
                value = checkoutDetails.totalAmount,
                currency = "USD"
            )
        }
    }
}

@Composable
fun CheckoutRowItem(title: String, value: Double, currency: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = title, fontSize = 16.sp, fontWeight = FontWeight.SemiBold, color = Color.Black)
        Row(verticalAlignment = Alignment.Bottom) {
            Text(
                text = StringUtils.formatCurrency(value),
                fontSize = 20.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.Black
            )
            Text(text = currency, fontSize = 16.sp, fontWeight = FontWeight.SemiBold, color = Color.LightGray)
        }
    }
}

@Composable
fun CheckoutButton(onCheckoutButtonClick: () -> Unit, modifier: Modifier = Modifier) {
    val brush = Brush.verticalGradient(
        listOf(
            Color.Transparent,
            Color.Transparent,
            Color.White.copy(alpha = .7f),
            Color.White.copy(alpha = .9f),
            Color.White,
        )
    )
    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(brush = brush)
    ) {
        ShadowButton(
            onClick = { onCheckoutButtonClick.invoke() },
            enabled = true,
            modifier = Modifier
                .padding(bottom = 50.dp)
                .fillMaxWidth(.7f)
                .height(56.dp)
                .align(Alignment.Center),
            shape = RoundedCornerShape(32.dp),
            containerColor = Orange,
            contentPadding = PaddingValues(vertical = 6.dp, horizontal = 6.dp),
            shadowColor = Orange.copy(alpha = .3f)
        ) {
            Text(
                text = "CHECKOUT",
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
                letterSpacing = 2.sp,
                color = Color.White
            )
        }
    }

}


@Preview
@Composable
private fun CartScreenPrev() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        Column {
            CartHeader(onBackClick = {})
            LinearProgressIndicator(
                modifier = Modifier
                    .padding(top = 6.dp)
                    .fillMaxWidth()
                    .height(4.dp),
                trackColor = Orange.copy(alpha = .1f),
                color = Orange
            )
            CartItemCard(
                cartItem = CartItem(
                    menuItemId = FoodItem(
                        arModelUrl = TODO(),
                        createdAt = TODO(),
                        description = TODO(),
                        id = TODO(),
                        imageUrl = TODO(),
                        name = TODO(),
                        price = TODO(),
                        restaurantId = TODO()
                    ),
                    addedAt = TODO(),
                    id = TODO(),
                    quantity = TODO(),
                    restaurantId = TODO(),
                    userId = TODO()
                ),
                onItemClicked = {},
                onRemoveClicked = {},
                onIncrementClicked = {

                },
                onDecrementClicked = {

                }
            )
            CheckoutBillSection(
                checkoutDetails = CheckoutDetails(
                    deliveryFee = 1.00,
                    subTotal = 27.30,
                    tax = 5.3,
                    totalAmount = 33.60
                )
            )
            CheckoutButton(onCheckoutButtonClick = {})
        }
    }
}