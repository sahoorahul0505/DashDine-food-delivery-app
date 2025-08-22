package com.kodebug.dashdine.ui.features.restaurant_detail

import android.annotation.SuppressLint
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.AbsoluteAlignment
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import coil3.compose.AsyncImage
import com.kodebug.dashdine.R
import com.kodebug.dashdine.components.ShadowButton
import com.kodebug.dashdine.data.models.FoodItem
import com.kodebug.dashdine.ui.features.food_items_details.FoodDetailScreen
import com.kodebug.dashdine.ui.navigation.Auth
import com.kodebug.dashdine.ui.navigation.FoodDetail
import com.kodebug.dashdine.ui.theme.Orange

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun SharedTransitionScope.RestaurantsDetailScreen(
    modifier: Modifier = Modifier,
    navController: NavController,
    name: String,
    imageUrl: String,
    restaurantID: String,
    animatedVisibilityScope: AnimatedVisibilityScope,
    viewModel: RestaurantViewModel = hiltViewModel()
) {
    LaunchedEffect(restaurantID) {
        viewModel.getFoodItemsForRestaurant(restaurantID)
    }
    val uiState = viewModel.uiState.collectAsState()
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(color = Color.White)
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 28.dp)
        ) {
            item {
                RestaurantDetailHeader(
                    imageUrl = imageUrl,
                    restaurantID = restaurantID,
                    onBackClick = { navController.popBackStack() },
                    onFavoriteClick = { },
                    animatedVisibilityScope = animatedVisibilityScope
                )
            }
            item {
                val description =
                    "DashDine is your smart food delivery companion, bringing your favorite meals from top restaurants straight to your doorstep. Whether you're craving local delights, healthy options, or late-night snacks, DashDine delivers fast, fresh, and reliably.\n" +
                            "\n" +
                            "Browse menus, track orders in real-time, and enjoy a seamless dining experience—all from the comfort of your phone."
                RestaurantDetailContent(
                    title = name,
                    description = description,
                    restaurantID = restaurantID,
                    animatedVisibilityScope = animatedVisibilityScope
                )
            }
            when (uiState.value) {
                is RestaurantViewModel.RestaurantState.Loading -> {
                    item {
                        LinearProgressIndicator(
                            trackColor = Orange,
                            color = Orange.copy(alpha = .5f),
                            modifier = Modifier
                                .padding(horizontal = 20.dp)
                                .fillMaxWidth()
                                .height(6.dp)
                                .align(alignment = Alignment.Center)
                        )
                    }
                }

                is RestaurantViewModel.RestaurantState.Error -> {
                    item {
                        Text(
                            text = "Failed to load food items.",
                            color = Color.Red,
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.Center
                        )
                    }
                }

                RestaurantViewModel.RestaurantState.Nothing -> {
                    null
                }

                is RestaurantViewModel.RestaurantState.Success -> {
                    val foodItems = (uiState.value as RestaurantViewModel.RestaurantState.Success).data
                    if (foodItems.isNotEmpty()) {
                        items(foodItems.chunked(2)) { foodRowItem ->
                            Row(
                                horizontalArrangement = Arrangement.SpaceBetween,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 20.dp)
                            ) {
                                for (foodItem in foodRowItem) {
                                    FoodMenuItem(foodItem = foodItem, onItemClick = {
                                        navController.navigate(FoodDetail(foodItem))
                                    }, animatedVisibilityScope = animatedVisibilityScope)
                                }
                                if (foodRowItem.size == 1) {
                                    Spacer(modifier = Modifier.weight(1f))
                                }
                            }
//                        Text(foodItem.name, textAlign = TextAlign.Center, modifier = modifier.padding(20.dp))
                        }
                    } else {
                        item {
                            Text(
                                text = "No Food Items Found",
                                modifier = Modifier.fillMaxWidth(),
                                textAlign = TextAlign.Center,
                                color = Color.Black
                            )
                        }
                    }
                }
            }
        }
//        val brush = Brush.verticalGradient(
//            listOf(
//                Orange.copy(alpha = .9f),
//                Color.Transparent
//            )
//        )
//        Box(modifier = modifier
//            .fillMaxWidth()
//            .height(60.dp)
//            .align(Alignment.TopCenter)
//            .background(brush = brush)
//        )
    }

}

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun SharedTransitionScope.RestaurantDetailHeader(
    imageUrl: String,
    restaurantID: String,
    onBackClick: () -> Unit,
    onFavoriteClick: () -> Unit,
    animatedVisibilityScope: AnimatedVisibilityScope
) {
    Box(
        modifier = Modifier
            .padding(start = 20.dp, end = 20.dp, top = 50.dp)
            .fillMaxWidth()
            .height(220.dp)
            .shadow(elevation = 10.dp, shape = RoundedCornerShape(16.dp), spotColor = Orange.copy(alpha = .9f))
            .clip(shape = RoundedCornerShape(14.dp))
    )
    {
//        Image(
//            painter = painterResource(id = R.drawable.welcome_background),
//            contentDescription = null,
//            contentScale = ContentScale.Crop,
//            modifier = Modifier
//                .fillMaxSize()
//                .clip(shape = RoundedCornerShape(14.dp))
//        )
        AsyncImage(
            model = imageUrl,
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxSize()
                .sharedElement(
                    sharedContentState = rememberSharedContentState(
                        key = "image/${restaurantID}"
                    ),
                    animatedVisibilityScope = animatedVisibilityScope
                )
                .clip(shape = RoundedCornerShape(16.dp))
        )
        FilledIconButton(
            onClick = onBackClick,
            colors = IconButtonDefaults.iconButtonColors(
                containerColor = Color.White,
                contentColor = Color.Black
            ),
            shape = RoundedCornerShape(14.dp),
            modifier = Modifier
                .padding(top = 12.dp, start = 12.dp)
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
        FilledIconButton(
            onClick = onFavoriteClick,
            colors = IconButtonDefaults.iconButtonColors(
                containerColor = Orange,
                contentColor = Color.White
            ),
            shape = RoundedCornerShape(14.dp),
            modifier = Modifier
                .padding(top = 12.dp, end = 12.dp)
                .size(40.dp)
                .shadow(
                    elevation = 20.dp,
                    shape = RoundedCornerShape(14.dp),
                    spotColor = Orange,
                    ambientColor = Orange
                )
                .align(alignment = Alignment.TopEnd),

            ) {
            Icon(
                imageVector = Icons.Default.Favorite,
                contentDescription = "favorite button",
                modifier = Modifier.size(24.dp)
            )
        }
    }
}


@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun SharedTransitionScope.RestaurantDetailContent(
    title: String,
    description: String,
    restaurantID: String,
    animatedVisibilityScope: AnimatedVisibilityScope
) {

    Column(
        modifier = Modifier
            .padding(horizontal = 20.dp, vertical = 24.dp)
            .fillMaxWidth()
    ) {
        Text(
            text = title,
            fontWeight = FontWeight.Bold,
            fontSize = 22.sp,
            color = Color.Black,
            modifier = Modifier.sharedElement(
                sharedContentState = rememberSharedContentState(key = "title/${restaurantID}"),
                animatedVisibilityScope = animatedVisibilityScope
            )
        )
        Spacer(modifier = Modifier.height(8.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            ShadowButton(
                onClick = {},
                containerColor = Color.White,
                shadowColor = Orange.copy(alpha = .1f),
                contentPadding = PaddingValues(horizontal = 6.dp, vertical = 3.dp),
                modifier = Modifier
                    .height(32.dp)
                    .fillMaxWidth(.3f)
                    .sharedElement(
                        sharedContentState = rememberSharedContentState(key = "rating/${restaurantID}"),
                        animatedVisibilityScope = animatedVisibilityScope
                    )
            ) {
                Text(text = "4.5", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                Spacer(modifier = Modifier.width(6.dp))
                Image(
                    painter = painterResource(id = R.drawable.ic_star),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(text = "(25+)", fontSize = 14.sp, color = Color.Gray)
            }
            Spacer(modifier = Modifier.width(18.dp))
            TextButton(onClick = {}) {
                Text(text = "View All Reviews>", fontSize = 16.sp, color = Orange)
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        Text(text = description, fontSize = 16.sp, color = Color.Gray, textAlign = TextAlign.Start)
    }
}

@SuppressLint("ConfigurationScreenWidthHeight")
@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun SharedTransitionScope.FoodMenuItem(
    foodItem: FoodItem,
    onItemClick: (FoodItem) -> Unit,
    animatedVisibilityScope: AnimatedVisibilityScope
) {
    val screenWidth = LocalConfiguration.current.screenWidthDp.dp
    val itemSpacing = 40.dp // total horizontal padding (20 left + 20 right)
    val itemWidth = (screenWidth - itemSpacing - 16.dp) / 2  // subtract item spacing + space between
    Box(
        modifier = Modifier
            .clickable(
                onClick = { onItemClick.invoke(foodItem) }
            )
            .padding(vertical = 14.dp)
            .height(230.dp)
            .width(width = itemWidth)
//            .width(174.dp)
            .shadow(elevation = 10.dp, shape = RoundedCornerShape(18.dp), spotColor = Orange.copy(alpha = .7f))
            .background(Color.White)
            .clip(RoundedCornerShape(18.dp))
    ) {
        Column(modifier = Modifier.fillMaxSize(), horizontalAlignment = Alignment.Start) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(.68f)
                    .clip(RoundedCornerShape(18.dp))
            ) {
//                Image(
//                    painter = painterResource(id = R.drawable.welcome_background),
//                    contentDescription = null,
//                    contentScale = ContentScale.Crop,
//                    modifier = Modifier.fillMaxSize()
//                )
                AsyncImage(
                    model = foodItem.imageUrl,
                    contentDescription = foodItem.name,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxSize()
                        .sharedElement(
                            sharedContentState = rememberSharedContentState(key = "image/${foodItem.id}"),
                            animatedVisibilityScope = animatedVisibilityScope
                        )
                        .clip(RoundedCornerShape(18.dp))
                )
                FilledIconButton(
                    onClick = { },
                    colors = IconButtonDefaults.iconButtonColors(
                        containerColor = Orange,
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier
                        .padding(top = 8.dp, end = 8.dp)
                        .size(28.dp)
                        .shadow(
                            elevation = 20.dp,
                            shape = RoundedCornerShape(14.dp),
                            spotColor = Orange,
                            ambientColor = Orange
                        )
                        .align(alignment = Alignment.TopEnd)
                        .sharedElement(
                            sharedContentState = rememberSharedContentState(key = "favorite/${foodItem.id}"),
                            animatedVisibilityScope = animatedVisibilityScope
                        )

                ) {
                    Icon(
                        imageVector = Icons.Default.Favorite,
                        contentDescription = "favorite button",
                        modifier = Modifier.size(16.dp)
                    )
                }
                ShadowButton(
                    onClick = {},
                    containerColor = Color.White,
                    shadowColor = Orange.copy(alpha = .2f),
                    contentPadding = PaddingValues(horizontal = 6.dp, vertical = 2.dp),
                    modifier = Modifier
                        .padding(top = 8.dp, start = 8.dp)
                        .align(alignment = Alignment.TopStart)
                        .height(28.dp)
                        .sharedElement(
                            sharedContentState = rememberSharedContentState(key = "price/${foodItem.id}"),
                            animatedVisibilityScope = animatedVisibilityScope
                        )
                        .fillMaxWidth(.45f)
                ) {
                    Text(text = "$", color = Orange, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.width(3.dp))
                    Text(
                        text = "${foodItem.price}",
                        color = Color.Black,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            Column(
                modifier = Modifier
                    .padding(start = 12.dp, top = 18.dp, end = 12.dp)
                    .fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = foodItem.name,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    color = Color.Black,
                    modifier = Modifier.sharedElement(
                        sharedContentState = rememberSharedContentState(key = "title/${foodItem.id}"),
                        animatedVisibilityScope = animatedVisibilityScope
                    )
                )
                Text(
                    text = foodItem.description,
                    fontSize = 14.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    color = Color.Gray,
                    modifier = Modifier.sharedElement(
                        sharedContentState = rememberSharedContentState(key = "description/${foodItem.id}"),
                        animatedVisibilityScope = animatedVisibilityScope
                    )
                )
            }

        }
        ShadowButton(
            onClick = {},
            containerColor = Color.White,
            shadowColor = Orange.copy(alpha = .2f),
            contentPadding = PaddingValues(horizontal = 3.dp, vertical = 1.dp),
            modifier = Modifier
                .align(alignment = Alignment.CenterStart)
                .padding(top = 78.dp, start = 8.dp)
                .height(28.dp)
                .sharedElement(
                    sharedContentState = rememberSharedContentState(key = "rating/${foodItem.id}"),
                    animatedVisibilityScope = animatedVisibilityScope
                )
                .fillMaxWidth(.5f)
        ) {
            Text(text = "4.5", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.Black)
            Spacer(modifier = Modifier.width(6.dp))
            Image(
                painter = painterResource(id = R.drawable.ic_star),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.size(10.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(text = "(25+)", fontSize = 10.sp, color = Color.Gray)
        }

    }
}