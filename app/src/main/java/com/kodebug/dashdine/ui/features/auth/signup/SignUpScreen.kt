package com.kodebug.dashdine.ui.features.auth.signup

import android.annotation.SuppressLint
import android.widget.Toast
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.util.trace
import androidx.compose.ui.window.Popup
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.kodebug.dashdine.R
import com.kodebug.dashdine.components.BouncingDots
import com.kodebug.dashdine.components.ShadowButton
import com.kodebug.dashdine.ui.DashDineErrorDialogBox
import com.kodebug.dashdine.ui.DashDineTextField
import com.kodebug.dashdine.ui.GroupSocialButtons
import com.kodebug.dashdine.ui.navigation.Auth
import com.kodebug.dashdine.ui.navigation.Home
import com.kodebug.dashdine.ui.navigation.Login
import com.kodebug.dashdine.ui.navigation.SignUp
import com.kodebug.dashdine.ui.theme.Orange
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


@SuppressLint("CoroutineCreationDuringComposition")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignUpScreen(
    modifier: Modifier = Modifier,
    navController: NavController,
    viewModel: SignUpViewModel = hiltViewModel()
) {
    val name = viewModel.name.collectAsStateWithLifecycle()
    val email = viewModel.email.collectAsStateWithLifecycle()
    val password = viewModel.password.collectAsStateWithLifecycle()
    val errorMessage = remember { mutableStateOf<String?>(null) }
    val loading = remember { mutableStateOf(false) }

    val sheetState = rememberModalBottomSheetState()
    val scope = rememberCoroutineScope()

    val uiState = viewModel.uiState.collectAsState()
    when (uiState.value) {
        SignUpViewModel.SignUpEvent.Error -> {
            loading.value = false
            errorMessage.value = "Failed to sign up"
            LaunchedEffect(true) {
                viewModel.navigationEvent.collectLatest { event ->
                    when (event) {
                        is SignUpViewModel.SignupNavigationEvent.ShowErrorDialog -> {
                            scope.launch {
                                sheetState.show()
                            }
                        }
                        else -> {

                        }
                    }
                }
            }
        }

        SignUpViewModel.SignUpEvent.Loading -> {
            loading.value = true
            errorMessage.value = null
        }

        else -> {
            loading.value = false
            errorMessage.value = null
        }
    }

    val context = LocalContext.current
    LaunchedEffect(true) {
        viewModel.navigationEvent.collectLatest { event ->
            when (event) {
                is SignUpViewModel.SignupNavigationEvent.NavigationToHome -> {
                    navController.navigate(Home) {
                        popUpTo(Auth) {
                            inclusive = true
                        }
                    }
                }

                is SignUpViewModel.SignupNavigationEvent.NavigationToLogin -> {
                    navController.navigate(Login) {
                        popUpTo(SignUp) {
                            inclusive = true
                        }
                    }
                }

                is SignUpViewModel.SignupNavigationEvent.ShowErrorDialog -> {
                    scope.launch {
                        sheetState.show()
                    }
                }
            }
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(color = Color.White)
    ) {
        Image(
            painter = painterResource(id = R.drawable.ic_auth_bg),
            contentDescription = null,
            contentScale = ContentScale.FillBounds,
            modifier = modifier.fillMaxSize()
        )
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(top = 50.dp, bottom = 40.dp, start = 30.dp, end = 30.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = modifier
                    .fillMaxWidth()
//                    .padding(bottom = 30.dp)
            )
            {
                FilledIconButton(
                    onClick = {
                        navController.navigate(Auth) {
                            popUpTo(Auth) {
                                inclusive = true
                            }
                        }
                    },
                    colors = IconButtonDefaults.iconButtonColors(
                        containerColor = Color.White,
                        contentColor = Color.Black
                    ),
                    shape = RoundedCornerShape(14.dp),
                    modifier = modifier
                        .size(50.dp)
                        .shadow(
                            elevation = 30.dp,
                            shape = RoundedCornerShape(14.dp),
                            spotColor = Color.Gray,
                            ambientColor = Color.Gray
                        )

                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_back_left),
                        contentDescription = "back button",
                        modifier = modifier.size(18.dp)
                    )
                }
            }
            Box(modifier = modifier.weight(1f))
            Text(
                text = stringResource(id = R.string.Sign_Up),
                fontSize = 38.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black, modifier = modifier.fillMaxWidth()
            )
            Spacer(modifier = modifier.size(16.dp))
            DashDineTextField(
                value = name.value,
                onValueChange = {
                    viewModel.onNameChange(it)
                },
                label = {
                    Text(
                        text = stringResource(R.string.full_name),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Normal,
                        color = Color.Gray
                    )
                },
                modifier = modifier
                    .fillMaxWidth()
                    .height(68.dp)
            )
            Spacer(modifier = modifier.size(12.dp))
            DashDineTextField(
                value = email.value,
                onValueChange = {
                    viewModel.onEmailChange(it)
                },
                label = {
                    Text(
                        text = stringResource(R.string.email),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Normal,
                        color = Color.Gray
                    )
                },
                modifier = modifier
                    .fillMaxWidth()
                    .height(68.dp)
            )
            Spacer(modifier = modifier.size(12.dp))
            DashDineTextField(
                value = password.value,
                onValueChange = {
                    viewModel.onPasswordChange(it)
                },
                label = {
                    Text(
                        text = stringResource(R.string.password),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Normal,
                        color = Color.Gray
                    )
                },
                trailingIcon = {
                    IconButton(
                        onClick = {}
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_eye),
                            contentDescription = "hide password",
                            modifier = modifier.size(24.dp)
                        )
                    }

                },
                visualTransformation = PasswordVisualTransformation(),
                modifier = modifier
                    .fillMaxWidth()
                    .height(68.dp)
            )
            Spacer(modifier = modifier.size(12.dp))
            Text(text = errorMessage.value ?: "", color = Color.Red)
            ShadowButton(
                onClick =
                    viewModel::onSignUpClick,
                modifier = modifier
                    .fillMaxWidth(.7f)
                    .height(64.dp),
                containerColor = Orange,
                shadowColor = Orange.copy(alpha = .3f)
            ) {
                Box {
                    AnimatedContent(
                        targetState = loading.value,
                        transitionSpec = {
//                            (slideInHorizontally { it } + fadeIn(animationSpec = tween(600))) togetherWith
//                                    (slideOutHorizontally { -it } + fadeOut(animationSpec = tween(600)))
                            fadeIn(animationSpec = tween(300)) + scaleIn(initialScale = 0.8f) togetherWith
                                    fadeOut(animationSpec = tween(300)) + scaleOut(targetScale = 0.8f)
                        }
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
                            Text(
                                text = stringResource(id = R.string.sign_up_button),
                                fontSize = 16.sp,
                                fontWeight = FontWeight.SemiBold,
                                letterSpacing = 2.sp,
                                color = Color.White
                            )
                        }
                    }
                }

            }
//            Button(
//                onClick = {},
//                modifier = modifier
//                    .height(64.dp)
//                    .fillMaxWidth(.7f)
//            ) {
//                Text(
//                    text = stringResource(R.string.sign_up_button),
//                    fontSize = 16.sp,
//                    fontWeight = FontWeight.SemiBold,
//                    letterSpacing = 2.sp,
//                    color = Color.White
//                )
//            }
            Spacer(modifier = modifier.height(20.dp))
            TextButton(
                onClick = {
                    viewModel.onLoginClick()
                },
                modifier = modifier.fillMaxWidth()
            ) {
                Row {
                    Text(
                        text = stringResource(id = R.string.already_have_account),
                        fontSize = 16.sp,
                        color = Color.Black
                    )
                    Spacer(modifier = modifier.width(6.dp))
                    Text(
                        text = stringResource(id = R.string.login),
                        fontSize = 16.sp,
                        textDecoration = TextDecoration.Underline
                    )
                }
            }
            Spacer(modifier = modifier.height(12.dp))
            GroupSocialButtons(
                title = R.string.sign_up_title,
                color = Color.Black,
                viewModel = viewModel
            )
        }
    }

    if (sheetState.isVisible) {
        ModalBottomSheet(onDismissRequest = {
            scope.launch {
                sheetState.hide()
            }
        }, sheetState = sheetState, containerColor = Color.White, tonalElevation = 10.dp) {
            DashDineErrorDialogBox(
                title = viewModel.error,
                description = viewModel.errorDescription,
                onDismiss = {
                    scope.launch {
                        sheetState.hide()
//                        showDialog = false
                    }
                }
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SignUpScreenPreview(modifier: Modifier = Modifier) {
    SignUpScreen(navController = rememberNavController())
}