package com.unovil.tardyscan

import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.camera.core.ExperimentalGetImage
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.core.view.WindowCompat
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.firebase.messaging.FirebaseMessaging
import com.unovil.tardyscan.data.network.internetcheck.AppUiState
import com.unovil.tardyscan.di.AuthNameManager
import com.unovil.tardyscan.di.ThemeManager
import com.unovil.tardyscan.presentation.feature.loading.LoadingScreen
import com.unovil.tardyscan.presentation.navigation.AuthNavigation
import com.unovil.tardyscan.presentation.navigation.MainNavigation
import com.unovil.tardyscan.presentation.navigation.ScanNavigation
import com.unovil.tardyscan.ui.theme.TardyScannerTheme
import dagger.hilt.android.AndroidEntryPoint
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.status.SessionStatus
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @Inject lateinit var supabaseClient: SupabaseClient
    @Inject lateinit var themeManager: ThemeManager
    @Inject lateinit var authNameManager: AuthNameManager
    private lateinit var cameraExecutor: ExecutorService

    @ExperimentalPermissionsApi
    @ExperimentalGetImage
    @ExperimentalAnimationApi
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        cameraExecutor = Executors.newSingleThreadExecutor()

        // create a notification channel
        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        val studentNotificationChannel = NotificationChannel(
            STUDENT_CHANNEL_ID,
            "Student Centric",
            NotificationManager.IMPORTANCE_HIGH
        ).also { it.description = "Student notifications, such as real-time attendance logs" }
        notificationManager.createNotificationChannel(studentNotificationChannel)

        setContent {
            val sessionStatus by supabaseClient.auth.sessionStatus.collectAsState()
            val isDarkTheme = themeManager.isDarkTheme.collectAsState()
            val authName = authNameManager.allowedUserName.collectAsState()
            val user = authNameManager.allowedUser.collectAsState()
            val isSystemInDarkTheme = isSystemInDarkTheme()
            val mainViewModel: MainViewModel = hiltViewModel()
            val isConnected by mainViewModel.isConnected.collectAsState()
            var isLoadingThemeFinished by remember { mutableStateOf(false) }
            var scanMode by rememberSaveable { mutableStateOf(false) }

            LaunchedEffect(Unit) {
                FirebaseMessaging.getInstance().token.addOnSuccessListener {
                    mainViewModel.saveFcmToken(it)
                    Log.d("MainActivity", "Token is $it")
                }
            }

            val appUiState = when {
                !isConnected -> AppUiState.NoInternet
                sessionStatus is SessionStatus.Initializing -> AppUiState.Loading
                sessionStatus is SessionStatus.RefreshFailure -> AppUiState.Loading
                sessionStatus is SessionStatus.NotAuthenticated -> AppUiState.NotAuthenticated
                sessionStatus is SessionStatus.Authenticated -> AppUiState.Authenticated
                else -> AppUiState.Loading
            }

            LaunchedEffect(Unit) {
                themeManager.loadTheme()
                isLoadingThemeFinished = true
            }

            LaunchedEffect(appUiState) {
                if (appUiState !is AppUiState.Loading) {
                    authNameManager.loadAllowedUser()
                }
            }

            LaunchedEffect(isDarkTheme.value, isSystemInDarkTheme) {
                val window = this@MainActivity.window
                val insetsController = WindowCompat.getInsetsController(window, window.decorView)
                when (isDarkTheme.value) {
                    true -> insetsController.isAppearanceLightStatusBars = false
                    false -> insetsController.isAppearanceLightStatusBars = true
                    null -> insetsController.isAppearanceLightStatusBars = !isSystemInDarkTheme
                }
            }

            if (isLoadingThemeFinished) {
                TardyScannerTheme(darkTheme = isDarkTheme.value ?: isSystemInDarkTheme) {
                    Log.d("MainActivity", "Theme loaded with darkTheme value ${isDarkTheme.value}")
                    Log.d("MainActivity", "Session value: $sessionStatus")

                    Surface(
                        modifier = Modifier.fillMaxSize(),
                        color = MaterialTheme.colorScheme.background
                    ) {
                        AnimatedContent(
                            targetState = appUiState,
                            label = "AppUiAnimatedContent",
                            transitionSpec = {
                                fadeIn(tween(200)) togetherWith fadeOut(tween(200))
                            }
                        ) { state ->
                            when (state) {
                                AppUiState.NoInternet -> LoadingScreen()
                                AppUiState.Loading -> LoadingScreen()
                                AppUiState.NotAuthenticated -> AuthNavigation {  }
                                AppUiState.Authenticated -> {
                                    AnimatedContent(
                                        targetState = scanMode,
                                        label = "ScanFade",
                                        transitionSpec = {
                                            fadeIn(tween(200)) togetherWith fadeOut(tween(200))
                                        }
                                    ) { isScanMode ->
                                        if (isScanMode) {
                                            ScanNavigation(cameraExecutor) { scanMode = false }
                                        } else {
                                            MainNavigation(
                                                authName = authName.value,
                                                user = user.value
                                            ) { scanMode = true }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        cameraExecutor.shutdown()
    }
}