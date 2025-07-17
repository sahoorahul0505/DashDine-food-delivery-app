package com.kodebug.dashdine.ui.features.auth

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.kodebug.dashdine.R
import com.kodebug.dashdine.ui.GroupSocialButtons
import com.kodebug.dashdine.ui.navigation.Auth
import com.kodebug.dashdine.ui.navigation.Home
import com.kodebug.dashdine.ui.navigation.Login
import com.kodebug.dashdine.ui.navigation.SignUp
import com.kodebug.dashdine.ui.theme.NightBlueDark
import com.kodebug.dashdine.ui.theme.Orange
import kotlinx.coroutines.flow.collectLatest

@Composable
fun AuthScreen(
    modifier: Modifier = Modifier,
    navController: NavController,
    viewModel: AuthViewModel = hiltViewModel()
) {

    LaunchedEffect(true) {
        viewModel.navigationEvent.collectLatest { event ->
            when (event) {
                is AuthViewModel.AuthNavigationEvent.NavigationToHome -> {
                    navController.navigate(Home) {
                        popUpTo(Auth) {
                            inclusive = true
                        }
                    }
                }

                is AuthViewModel.AuthNavigationEvent.NavigationToLogin -> {
                    navController.navigate(Login) {
//                        popUpTo(Auth) {
//                            inclusive = true
//                        }
                    }
                }

                is AuthViewModel.AuthNavigationEvent.NavigationToSignUp -> {
                    navController.navigate(SignUp) {
//                        popUpTo(Auth) {
//                            inclusive = true
//                        }
                    }
                }
            }
        }
    }
    val imageSize = remember {
        mutableStateOf(IntSize.Zero)
    }
    val brush = Brush.verticalGradient(
        colors = listOf(
            Color.Transparent,
            Color.Black
        ),
        startY = imageSize.value.height.toFloat() / 3
    )
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(color = Color.Black)
    )
    {
        Image(
            painter = painterResource(id = R.drawable.welcome_background),
            contentDescription = null,
            contentScale = ContentScale.FillBounds,
            modifier = modifier
                .fillMaxSize()
                .onGloballyPositioned {
                    imageSize.value = it.size
                }
                .alpha(0.7f)
        )
        Box(
            modifier = modifier
                .matchParentSize()
                .background(brush = brush)
        )
        Button(
            onClick = {},
            colors = ButtonDefaults.buttonColors(containerColor = Color.White, contentColor = Orange),
            modifier = modifier
                .align(alignment = Alignment.TopEnd)
                .padding(top = 50.dp, end = 30.dp)
        ) {
            Text(text = stringResource(id = R.string.skip))
        }
        Column(
            modifier = modifier
                .fillMaxWidth()
                .padding(top = 170.dp, start = 30.dp, end = 30.dp)
        )
        {
            Text(
                text = stringResource(id = R.string.welcome_to),
                fontSize = 42.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black,
            )

//            Spacer(modifier = modifier.height(6.dp))
            Text(
                text = stringResource(id = R.string.app_name),
                fontSize = 42.sp,
                fontWeight = FontWeight.Bold,
                color = Orange
            )
            Spacer(modifier = modifier.height(12.dp))
            Text(
                text = stringResource(id = R.string.dashdine_desc),
                fontSize = 18.sp,
                fontWeight = FontWeight.Normal,
                color = NightBlueDark
            )
        }
        Column(
            modifier = modifier
                .fillMaxWidth()
                .align(alignment = Alignment.BottomCenter)
                .padding(30.dp)
        ) {
//            Spacer(modifier = modifier.height(24.dp))
            GroupSocialButtons(title = R.string.sign_in_title, viewModel = viewModel)
            Spacer(modifier = modifier.height(32.dp))
            Button(
                onClick = {
                    viewModel.onSignUpClick()
                },
                modifier = modifier
                    .fillMaxWidth()
                    .height(64.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Gray.copy(alpha = .4f)
                ),
                shape = RoundedCornerShape(50.dp),
                border = BorderStroke(width = 1.dp, color = Color.White)
            ) {
                Text(
                    text = stringResource(R.string.sign_up_with_email),
                    fontSize = 18.sp,
                    color = Color.White,
                )

            }
            Spacer(modifier = modifier.height(12.dp))
            TextButton(
                onClick = {
                    viewModel.onLoginClick()
                }
            ) {
                Row(modifier = modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                    Text(
                        text = stringResource(R.string.already_have_account),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Normal,
                        color = Color.White

                    )
                    Spacer(modifier = modifier.width(6.dp))
                    Text(
                        text = stringResource(R.string.sign_in),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Normal,
                        color = Color.White,
                        textDecoration = TextDecoration.Underline
                    )

                }

            }
        }
    }
}


@Preview(showBackground = true)
@Composable
private fun AuthScreenPreview() {
    AuthScreen(navController = rememberNavController())
}