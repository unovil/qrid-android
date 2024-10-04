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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
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
                modifier = Modifier.fillMaxSize().padding(20.dp),
                horizontalAlignment = Alignment.Start,
                verticalArrangement = Arrangement.Top
            ) {
                val toast = Toast.makeText(LocalContext.current, "Account pressed", Toast.LENGTH_SHORT)
                Text(
                    "Settings",
                    fontSize = 40.sp
                )
                Spacer(modifier = Modifier.height(20.dp))
                SettingsMainItem(
                    mainLabel = "Account",
                    subLabel = "View account information or logout",
                    icon = Icons.Default.AccountCircle
                ) {
                    toast.show()
                }
                SettingsMainItem(
                    mainLabel = "Appearance",
                    subLabel = "Change the app's look and feel",
                    icon = Icons.Default.Palette
                ) {}
                SettingsMainItem(
                    mainLabel = "Other Preferences",
                    icon = Icons.Default.Settings
                ) {}
                SettingsMainItem(
                    mainLabel = "About Tardy Scanner",
                    icon = Icons.Default.Info
                ) {}
            }
        }
    }
}

@Composable
fun SettingsMainItem(mainLabel: String, subLabel: String? = null, icon: ImageVector, onClick: () -> Unit) {
    TardyScannerTheme {
        Surface(
            shape = RoundedCornerShape(20.dp),
            modifier = Modifier.fillMaxWidth().padding(bottom = 15.dp),
            onClick = onClick,
            color = Color.LightGray
        ) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(vertical = 15.dp, horizontal = 15.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        icon,
                        modifier = Modifier.size(40.dp),
                        contentDescription = mainLabel
                    )
                    Spacer(modifier = Modifier.size(10.dp))
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
    SettingsMainItem(
        mainLabel = "Account",
        subLabel = "View account information or logout",
        icon = Icons.Default.AccountCircle
    ) {

    }
}