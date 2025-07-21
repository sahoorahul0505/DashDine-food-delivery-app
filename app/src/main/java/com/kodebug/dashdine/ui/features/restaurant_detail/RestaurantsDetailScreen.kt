package com.kodebug.dashdine.ui.features.restaurant_detail

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
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
import com.kodebug.dashdine.ui.navigation.Auth
import com.kodebug.dashdine.ui.theme.Orange

@Composable
fun RestaurantsDetailScreen(
    modifier: Modifier = Modifier,
    navController: NavController,
    name: String,
    imageUrl: String,
    restaurantID: String,
    viewModel: RestaurantViewModel = hiltViewModel()
) {
    LaunchedEffect(restaurantID) {
        viewModel.getFoodItemsForRestaurant(restaurantID)
    }
    val uiState = viewModel.uiState.collectAsState()
    Box(modifier = modifier
        .fillMaxSize()
        .background(color = Color.White)) {
        LazyColumn {
            item {
                RestaurantDetailHeader(
                    imageUrl = imageUrl,
                    onBackClick = { navController.popBackStack() },
                    onFavoriteClick = { })
            }
            item {
                val description =
                    "DashDine is your smart food delivery companion, bringing your favorite meals from top restaurants straight to your doorstep. Whether you're craving local delights, healthy options, or late-night snacks, DashDine delivers fast, fresh, and reliably.\n" +
                            "\n" +
                            "Browse menus, track orders in real-time, and enjoy a seamless dining experience—all from the comfort of your phone."
                RestaurantDetailContent(title = name, description = description)
            }
            when (uiState.value) {
                is RestaurantViewModel.RestaurantState.Loading -> {
                    item {
                        Text(text = "Loading")
                    }
                }

                is RestaurantViewModel.RestaurantState.Error -> {
                    item {
                        Text(text = "Error")
                    }
                }

                RestaurantViewModel.RestaurantState.Nothing -> {
                    null
                }

                is RestaurantViewModel.RestaurantState.Success -> {
                    val foodItems = (uiState.value as RestaurantViewModel.RestaurantState.Success).data
                    items(foodItems) { foodItem ->
                        Text(foodItem.name, textAlign = TextAlign.Center, modifier = modifier.padding(20.dp))
//                    FoodMenuItem(foodItems = foodItems, onItemClick = {})
                    }
                }
            }
        }
    }

}

@Composable
fun RestaurantDetailHeader(
    imageUrl: String,
    onBackClick: () -> Unit,
    onFavoriteClick: () -> Unit,
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


@Composable
fun RestaurantDetailContent(
    title: String,
    description: String
) {

    Column(
        modifier = Modifier
            .padding(horizontal = 20.dp, vertical = 24.dp)
            .fillMaxWidth()
    ) {
        Text(text = title, fontWeight = FontWeight.Bold, fontSize = 22.sp, color = Color.Black)
        Spacer(modifier = Modifier.height(8.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            ShadowButton(
                onClick = {},
                containerColor = Color.White,
                shadowColor = Orange.copy(alpha = .1f),
                contentPadding = PaddingValues(horizontal = 6.dp, vertical = 6.dp),
                modifier = Modifier
                    .height(32.dp)
                    .fillMaxWidth(.3f)
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

@Composable
fun FoodMenuItem(foodItems: FoodItem, onItemClick: (FoodItem) -> Unit) {

}


@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun RestaurantsDetailPreview() {
    RestaurantsDetailScreen(imageUrl = "", name = "", restaurantID = "", navController = rememberNavController())
}