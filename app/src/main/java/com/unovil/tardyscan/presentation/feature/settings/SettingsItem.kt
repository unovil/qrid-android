package com.unovil.tardyscan.presentation.feature.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.unovil.tardyscan.ui.theme.TardyScannerTheme

@Composable
fun SettingsItem(mainLabel: String, subLabel: String? = null, showChevron: Boolean = true, color: Color = Color.Unspecified, onClick: () -> Unit = { }) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        onClick = onClick
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 28.dp, vertical = 15.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                horizontalAlignment = Alignment.Start,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    mainLabel,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = color
                )
                if (subLabel != null && subLabel.isNotEmpty()) Text(
                    subLabel,
                    style = typography.labelMedium,
                    color = color
                )
            }

            if (showChevron) {
                Icon(
                    Icons.Default.ChevronRight,
                    tint = if (color == Color.Unspecified) LocalContentColor.current else color,
                    contentDescription = "View setting"
                )
            }
        }
    }
}

@Composable
@Preview
fun SettingsMainItemPreview() {
    TardyScannerTheme {
        SettingsItem(
            mainLabel = "Account",
            subLabel = "View account information or logout",
        ) { }
    }
}