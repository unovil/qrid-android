package com.unovil.tardyscan

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.camera.core.ExperimentalGetImage
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
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
            var scanMode by rememberSaveable { mutableStateOf(false) }

            TardyScannerTheme(darkTheme = isDarkTheme.value) {
                when (sessionStatus.value) {
                    is SessionStatus.Authenticated -> {
                        if (scanMode) {
                            ScanNavigation(cameraExecutor) { scanMode = false }
                        } else {
                            MainNavigation { scanMode = true }
                        }
                    }

                    is SessionStatus.Initializing, is SessionStatus.RefreshFailure -> { /* splash */ }

                    is SessionStatus.NotAuthenticated -> { AuthNavigation {  } }
                }
            }

        }
    }

    override fun onDestroy() {
        super.onDestroy()
        cameraExecutor.shutdown()
    }
}