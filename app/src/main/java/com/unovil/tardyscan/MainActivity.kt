package com.unovil.tardyscan

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.camera.core.ExperimentalGetImage
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.core.view.WindowCompat
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.unovil.tardyscan.di.ThemeManager
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
    private lateinit var cameraExecutor: ExecutorService

    @ExperimentalPermissionsApi
    @ExperimentalGetImage
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        cameraExecutor = Executors.newSingleThreadExecutor()

        setContent {
            val sessionStatus = supabaseClient.auth.sessionStatus.collectAsState()
            val isDarkTheme = themeManager.isDarkTheme.collectAsState()
            val isSystemInDarkTheme = isSystemInDarkTheme()
            var isLoadingThemeFinished by remember { mutableStateOf(false) }
            var scanMode by rememberSaveable { mutableStateOf(false) }

            LaunchedEffect(Unit) {
                themeManager.loadTheme()
                isLoadingThemeFinished = true
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
                    when (sessionStatus.value) {
                        is SessionStatus.Authenticated -> {
                            AnimatedContent(
                                targetState = scanMode,
                                label = "ScanFade",
                                transitionSpec = {
                                    fadeIn(tween(200)).togetherWith(
                                        fadeOut(tween(200))
                                    )
                                }
                            ) { isScanMode ->
                                if (isScanMode) {
                                    ScanNavigation(cameraExecutor) { scanMode = false }
                                } else {
                                    MainNavigation { scanMode = true }
                                }
                            }
                        }

                        is SessionStatus.Initializing, is SessionStatus.RefreshFailure -> { /* splash */ }

                        is SessionStatus.NotAuthenticated -> { AuthNavigation {  } }
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