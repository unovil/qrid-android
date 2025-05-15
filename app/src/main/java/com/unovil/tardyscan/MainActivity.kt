package com.unovil.tardyscan

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.camera.core.ExperimentalGetImage
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.unovil.tardyscan.presentation.navigation.MainNavigation
import com.unovil.tardyscan.ui.theme.TardyScannerTheme
import dagger.hilt.android.AndroidEntryPoint
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.status.SessionStatus
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @Inject
    lateinit var supabaseClient: SupabaseClient

    @ExperimentalPermissionsApi
    @ExperimentalGetImage
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
        setContent {
            val sessionStatus = supabaseClient.auth.sessionStatus.collectAsState()
            var showMainActivity by remember { mutableStateOf(false) }

            LaunchedEffect(sessionStatus.value, showMainActivity) {
                Log.d("MainActivity", "Session status is: ${sessionStatus.value}")
                when (sessionStatus.value) {
                    is SessionStatus.Authenticated -> {
                        showMainActivity = true
                    }

                    is SessionStatus.Initializing -> {
                        // run a loading screen here, hopefully
                    }

                    else -> {
                        Log.d("MainActivity", "session status: not authenticated")
                        this@MainActivity.startActivity(
                            Intent(
                                this@MainActivity,
                                AuthActivity::class.java
                            )
                        )
                        finish()
                    }
                }
            }

            if (showMainActivity) {
                TardyScannerTheme {
                    MainNavigation {
                        this@MainActivity.startActivity(
                            Intent(
                                this@MainActivity,
                                ScanActivity::class.java
                            )
                        )
                    }
                }
            }
        }
    }
}