package com.unovil.tardyscan.screens

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.unovil.tardyscan.ui.theme.TardyScannerTheme

@Composable
@Preview
fun SettingsScreen(navController: NavController = rememberNavController()) {
    TardyScannerTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            Column (
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.Start,
                verticalArrangement = Arrangement.Top
            ) {
                Spacer(modifier = Modifier.fillMaxHeight(0.15f))
                Text(
                    "Settings",
                    modifier = Modifier.padding(horizontal = 20.dp),
                    style = MaterialTheme.typography.headlineLarge
                )
                Spacer(modifier = Modifier.height(20.dp))
                Text(
                    "Integrations",
                    modifier = Modifier.padding(horizontal = 20.dp),
                    color = Color(78, 89, 122),
                    style = MaterialTheme.typography.labelLarge
                )
                val toast = Toast.makeText(LocalContext.current, "Account pressed", Toast.LENGTH_SHORT)
                SettingsItem(
                    mainLabel = "Account",
                    subLabel = "View account information or logout",
                    icon = Icons.Default.AccountCircle
                ) {
                    toast.show()
                }
                SettingsItem(
                    mainLabel = "Appearance",
                    subLabel = "Change the app's look and feel",
                    icon = Icons.Default.Palette
                ) {}
                SettingsItem(
                    mainLabel = "Other Preferences",
                    icon = Icons.Default.Settings
                ) {}
                SettingsItem(
                    mainLabel = "About Tardy Scanner",
                    icon = Icons.Default.Info
                ) {}
            }
        }
    }
}

@Composable
fun SettingsItem(mainLabel: String, subLabel: String? = null, icon: ImageVector, onClick: () -> Unit) {
    TardyScannerTheme {
        Surface(
            modifier = Modifier.fillMaxWidth(),
            onClick = onClick
        ) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(vertical = 15.dp, horizontal = 15.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        modifier = Modifier.padding(horizontal = 15.dp)
                    ) {
                        Icon(
                            icon,
                            modifier = Modifier.size(20.dp),
                            contentDescription = mainLabel
                        )
                    }
                    Column(
                        horizontalAlignment = Alignment.Start,
                        verticalArrangement = Arrangement.Center


                    ) {
                        Text(
                            mainLabel,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )
                        if (subLabel != null && subLabel.isNotEmpty()) Text(
                            subLabel,
                            style = MaterialTheme.typography.labelMedium
                        )
                    }
                }
                Icon(
                    Icons.Default.ChevronRight,
                    contentDescription = "View setting"
                )
            }
        }
    }
}

@Composable
@Preview
fun SettingsMainItemPreview() {
    SettingsItem(
        mainLabel = "Account",
        subLabel = "View account information or logout",
        icon = Icons.Default.AccountCircle
    ) {

    }
}