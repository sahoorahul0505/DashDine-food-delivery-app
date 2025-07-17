package com.kodebug.dashdine

import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.animation.OvershootInterpolator
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.modifier.modifierLocalOf
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.animation.doOnEnd
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.kodebug.dashdine.data.DashDineApiService
import com.kodebug.dashdine.ui.features.auth.AuthScreen
import com.kodebug.dashdine.ui.features.auth.login.LoginScreen
import com.kodebug.dashdine.ui.features.auth.signup.SignUpScreen
import com.kodebug.dashdine.ui.navigation.Auth
import com.kodebug.dashdine.ui.navigation.Home
import com.kodebug.dashdine.ui.navigation.Login
import com.kodebug.dashdine.ui.navigation.SignUp
import com.kodebug.dashdine.ui.theme.DashDineTheme
import com.kodebug.dashdine.ui.theme.Orange
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    var showSplashScreen = true

    //    @Inject
//    late init var dashDineApiService: DashDineApiService
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen().apply {
            setKeepOnScreenCondition {
                showSplashScreen
            }
            setOnExitAnimationListener { screen ->
                val zoomX = ObjectAnimator.ofFloat(
                    screen.iconView,
                    View.SCALE_X,
                    0.5f,
                    0.1f
                )
                val zoomY = ObjectAnimator.ofFloat(
                    screen.iconView,
                    View.SCALE_Y,
                    0.5f,
                    0.1f
                )
                zoomX.duration = 500
                zoomY.duration = 500
                zoomX.interpolator = OvershootInterpolator()
                zoomY.interpolator = OvershootInterpolator()
                zoomX.doOnEnd {
                    screen.remove()
                }
                zoomY.doOnEnd {
                    screen.remove()
                }
                zoomX.start()
                zoomY.start()
            }
        }
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            DashDineTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Box(modifier = Modifier.padding(innerPadding))
                    val navController = rememberNavController()
                    NavHost(
                        navController = navController, startDestination = Auth,
                        enterTransition = {
                            slideIntoContainer(
                                towards = AnimatedContentTransitionScope.SlideDirection.Start,
                                animationSpec = tween(600)
                            ) + fadeIn(animationSpec = tween(1000))
                        },
                        exitTransition = {
                            slideOutOfContainer(
                                towards = AnimatedContentTransitionScope.SlideDirection.End,
                                animationSpec = tween(600)
                            ) + fadeOut(animationSpec = tween(1000))
                        },
                        popEnterTransition = {
                            slideIntoContainer(
                                towards = AnimatedContentTransitionScope.SlideDirection.Up,
                                animationSpec = tween(600)
                            ) + fadeIn(animationSpec = tween(1000))
                        },
                        popExitTransition = {
                            slideOutOfContainer(
                                towards = AnimatedContentTransitionScope.SlideDirection.Down,
                                animationSpec = tween(600)
                            ) + fadeOut(animationSpec = tween(1000))
                        }
                    ) {
                        composable<Auth> {
                            AuthScreen(navController = navController)
                        }
                        composable<SignUp> {
                            SignUpScreen(navController = navController)
                        }
                        composable<Login> {
                            LoginScreen(navController = navController)
                        }
                        composable<Home> {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) { Text(text = "Home") }
                        }
                    }


//                    AuthScreen()
//                    SignUpScreen()
//                    LoginScreen()

                }
            }
        }
//        if (::dashDineApiService.isInitialized) {
//            Log.d("ApiService", "ApiService is initialized")
//        }
        CoroutineScope(Dispatchers.IO).launch {
            delay(2000)
            showSplashScreen = false
        }
    }
}