package com.unovil.tardyscan

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.unovil.tardyscan.presentation.navigation.AuthNavigation
import com.unovil.tardyscan.ui.theme.TardyScannerTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AuthActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            TardyScannerTheme {
                AuthNavigation {
                    this@AuthActivity.startActivity(
                        Intent(
                            this@AuthActivity,
                            MainActivity::class.java
                        )
                    )
                    finish()
                }
            }
        }
    }
}