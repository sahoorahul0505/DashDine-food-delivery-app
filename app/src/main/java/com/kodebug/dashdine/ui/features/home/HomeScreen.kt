package com.kodebug.dashdine.ui.features.home

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.AbsoluteAlignment
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil3.compose.AsyncImage
import com.kodebug.dashdine.R
import com.kodebug.dashdine.components.ItemCardLarge
import com.kodebug.dashdine.components.ItemCardMini
import com.kodebug.dashdine.components.ShadowButton
import com.kodebug.dashdine.data.models.Category
import com.kodebug.dashdine.data.models.Restaurant
import com.kodebug.dashdine.ui.features.restaurant_detail.RestaurantsDetailScreen
import com.kodebug.dashdine.ui.navigation.RestaurantDetail
import com.kodebug.dashdine.ui.theme.Orange
import kotlinx.coroutines.flow.collectLatest
import java.nio.file.WatchEvent

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun SharedTransitionScope.HomeScreen(
    modifier: Modifier = Modifier,
    navController: NavController,
    animatedVisibilityScope: AnimatedVisibilityScope,
    viewModel: HomeViewModel = hiltViewModel()
) {

    val uiState = viewModel.uiState.collectAsState()
    LaunchedEffect(Unit) {
        viewModel.navigationEvent.collectLatest {
            when (it) {
                is HomeViewModel.HomeScreenNavigationEvent.NavigationToDetail -> {
                    navController.navigate(
                        RestaurantDetail(
                            it.restaurantID,
                            it.restaurantName,
                            it.restaurantImageUrl,
                        )
                    )
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
                .fillMaxWidth()
                .padding(top = 50.dp),
        ) {
            when (uiState.value) {
                HomeViewModel.HomeScreenState.Loading -> {
                    Text(text = "Loading")
                }

                HomeViewModel.HomeScreenState.Error -> {
                    Text(text = "${uiState.value}")
                }

                HomeViewModel.HomeScreenState.Success -> {
                    val categories = viewModel.categories
                    CategoriesList(
                        categories = categories,
                        onCategorySelected = {
//                            navController.navigate("category/${it.id}")
                        },
                        selectedItem = categories.first()
                    )
                    val restaurants = viewModel.restaurants
                    RestaurantsList(
                        restaurants = restaurants,
                        onRestaurantSelected = {
                            viewModel.navigateToRestaurantDetail(it)
                        },
                        animatedVisibilityScope = animatedVisibilityScope
                    )
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

@Composable
fun CategoriesList(categories: List<Category>, onCategorySelected: (Category) -> Unit, selectedItem: Category) {
    LazyRow(
        modifier = Modifier.height(166.dp),
        horizontalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        item {
            Spacer(modifier = Modifier.width(4.dp))
        }
        items(categories) { cat ->
            CategoryItem(
                category = cat,
                onCategorySelected = { onCategorySelected(it) },
                isSelectedItem = cat == selectedItem
            )
        }
        item {
            Spacer(modifier = Modifier.width(4.dp))
        }
    }


}


@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun SharedTransitionScope.RestaurantsList(
    restaurants: List<Restaurant>,
    onRestaurantSelected: (Restaurant) -> Unit,
    animatedVisibilityScope: AnimatedVisibilityScope
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Popular Restaurants",
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.Black
            )
            TextButton(onClick = {}) {
                Text(text = "View All >", fontSize = 18.sp, color = Orange)
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            item {
//                Spacer(modifier = Modifier.width(2.dp))
            }
            items(restaurants) { rest ->
                RestaurantItem(
                    restaurant = rest,
                    onRestaurantSelected = { onRestaurantSelected(rest) },
                    animatedVisibilityScope = animatedVisibilityScope
                )
            }
            item {
//                Spacer(modifier = Modifier.width(4.dp))
            }
        }
    }
}

@Composable
fun CategoryItem(category: Category, onCategorySelected: (Category) -> Unit, isSelectedItem: Boolean) {

    val containerColor by animateColorAsState(
        targetValue = if (isSelectedItem) Orange else Color.White,
        label = "bgColor"
    )
    val shadowColor = if (isSelectedItem) Orange.copy(alpha = .5f) else Color.LightGray.copy(alpha = .5f)

    ItemCardMini(
        item = category,
        onClick = { onCategorySelected(it) },
        shadowColor = shadowColor,
        containerColor = containerColor,
        shape = RoundedCornerShape(100.dp),
        shadowCornerRadius = 65.dp,
        modifier = Modifier
    ) {
        Column(
            modifier = Modifier
                .height(136.dp)
                .width(76.dp)
                .padding(horizontal = 4.dp, vertical = 6.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
//            Box(
//                modifier = Modifier
//                    .size(48.dp)
//                    .clip(shape = CircleShape)
//                    .background(color = Color.White)
//            ) {
            AsyncImage(
                model = category.imageUrl,
                contentDescription = category.name,
                modifier = Modifier
                    .size(58.dp)
                    .background(Color.White, shape = CircleShape)
                    .shadow(
                        elevation = 16.dp, spotColor = Orange, ambientColor = Orange, shape = CircleShape
                    )
                    .clip(CircleShape),
                contentScale = ContentScale.Inside
            )
//            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                category.name.split(" ").first(),
                fontSize = 12.sp,
                textAlign = TextAlign.Center,
                color = if (isSelectedItem) Color.White else Color.Black
            )
        }

    }
}


@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun SharedTransitionScope.RestaurantItem(
    restaurant: Restaurant,
    onRestaurantSelected: (Restaurant) -> Unit,
    animatedVisibilityScope: AnimatedVisibilityScope
) {
    ItemCardLarge(
        item = restaurant,
        onClick = { onRestaurantSelected(it) },
        shadowColor = Color.LightGray.copy(alpha = .4f),
        shape = RoundedCornerShape(20.dp),
        shadowCornerRadius = 20.dp,
        modifier = Modifier
    ) {
        Box(
            modifier = Modifier
                .width(278.dp)
                .height(220.dp)
        )
        {
            Column(modifier = Modifier.fillMaxWidth()) {
                AsyncImage(
                    model = restaurant.imageUrl,
                    contentDescription = restaurant.name,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(130.dp)
                        .sharedElement(
                            sharedContentState = rememberSharedContentState(key = "image/${restaurant.id}"),
                            animatedVisibilityScope = animatedVisibilityScope
                        )
                        .clip(shape = RoundedCornerShape(20.dp))
                )
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(14.dp), horizontalAlignment = Alignment.Start
                ) {
                    Text(
                        text = restaurant.name,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.Black,
                        modifier = Modifier.sharedElement(
                            sharedContentState = rememberSharedContentState(key = "title/${restaurant.id}"),
                            animatedVisibilityScope = animatedVisibilityScope
                        )
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Image(
                                painter = painterResource(id = R.drawable.ic_delivery_bick),
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(text = "Free Delivery", fontSize = 14.sp, color = Color.Gray)
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Image(
                                painter = painterResource(id = R.drawable.ic_watch),
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(text = "20-30 min", fontSize = 14.sp, color = Color.Gray)
                        }
                    }
                }
            }
            Box(
                modifier = Modifier
                    .padding(12.dp)
                    .align(alignment = Alignment.TopStart)
            ) {
                ShadowButton(
                    onClick = {},
                    containerColor = Color.White,
                    shadowColor = Orange.copy(alpha = .1f),
                    contentPadding = PaddingValues(horizontal = 6.dp, vertical = 6.dp),
                    modifier = Modifier
                        .height(32.dp)
                        .fillMaxWidth(.45f)
                        .sharedElement(
                            sharedContentState = rememberSharedContentState(key = "rating/${restaurant.id}"),
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
            }
        }
    }
}

//@Preview(showBackground = true)
//@Composable
//private fun CategoriesListPreview() {
//    Column(
//        modifier = Modifier.fillMaxSize(),
//        verticalArrangement = Arrangement.Center,
//        horizontalAlignment = Alignment.CenterHorizontally
//    ) {
//        CategoryItem(
//            category = Category(
//                "",
//                "",
//                "https://images.vexels.com/content/136312/preview/logo-pizza-fast-food-d65bfe.png",
//                "Pizza"
//            ), onCategorySelected = {}, isSelectedItem = true
//        )
//    }
//}