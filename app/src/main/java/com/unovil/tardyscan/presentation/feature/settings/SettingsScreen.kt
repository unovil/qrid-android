package com.unovil.tardyscan.presentation.feature.settings

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
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.unovil.tardyscan.ui.theme.TardyScannerTheme
import kotlinx.coroutines.flow.MutableStateFlow

@Composable
fun SettingsScreen(
    settingsViewModel: SettingsViewModel? = hiltViewModel(),
    appearanceList: List<String> = settingsViewModel!!.appearanceList,
    newAppearance: State<String> = settingsViewModel!!.newAppearance.collectAsState(),
    selectedAppearance: State<String> = settingsViewModel!!.selectedAppearance.collectAsState(),
    onUpdateAppearance: (String) -> Unit = settingsViewModel!!::onUpdateAppearance,
    onCancelAppearance: () -> Unit = settingsViewModel!!::onCancelAppearance,
    onSetAppearance: () -> Unit = settingsViewModel!!::onSetAppearance,
    onLogOut: (onFailure: () -> Unit) -> Unit = settingsViewModel!!::onLogOut
) {
    var isOpenedAppearanceDialog by remember { mutableStateOf(false) }
    val context = LocalContext.current

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

            SettingsItem(
                mainLabel = "Account",
                subLabel = "View account information or logout",
            ) {}

            SettingsItem(
                mainLabel = "Appearance",
                subLabel = "Currently set to: ${selectedAppearance.value}",
            ) { isOpenedAppearanceDialog = !isOpenedAppearanceDialog }

            SettingsItem(
                mainLabel = "Other Preferences"
            ) {}

            SettingsItem(
                mainLabel = "Log out"
            ) { onLogOut {
                Toast.makeText(context, "Something went wrong.", Toast.LENGTH_SHORT).show()
            }}

            SettingsItem(
                mainLabel = "About Tardy Scanner"
            ) {}
        }
    }

    if (isOpenedAppearanceDialog) {
        AlertDialog(
            icon = { Icon(Icons.Default.Palette, contentDescription = "Appearance") },
            title = { Text("Appearance") },
            text = {
                Column(
                    modifier = Modifier.selectableGroup(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    appearanceList.forEach { text ->
                        Row(
                            modifier = Modifier.fillMaxWidth().selectable(
                                selected = (text == newAppearance.value),
                                onClick = { onUpdateAppearance(text) }
                            ),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = (text == newAppearance.value),
                                onClick = null // for accessibility choo choo
                            )
                            Text(
                                text = text,
                                modifier = Modifier.padding(start = 16.dp)
                            )
                        }
                    }
                }
            },
            onDismissRequest = {
                isOpenedAppearanceDialog = false
                onCancelAppearance()
            },
            confirmButton = {
                TextButton(onClick = {
                    isOpenedAppearanceDialog = false
                    onSetAppearance()
                }) {
                    Text("Confirm")
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    isOpenedAppearanceDialog = false
                    onCancelAppearance()
                }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
@Preview
fun SettingsScreenPreview() {
    val appearanceList = listOf("☀️ Light mode", "🌙 Dark mode", "⚙️ Follow system setting")
    val selectedAppearance = MutableStateFlow(appearanceList[0])

    TardyScannerTheme {
        SettingsScreen(
            settingsViewModel = null,
            appearanceList,
            selectedAppearance.collectAsState()
        )
    }
}