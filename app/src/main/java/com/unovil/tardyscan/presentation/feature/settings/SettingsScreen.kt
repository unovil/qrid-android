package com.unovil.tardyscan.presentation.feature.settings

import android.content.Intent
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
import androidx.compose.material.icons.filled.Person
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.hilt.navigation.compose.hiltViewModel
import com.unovil.tardyscan.data.network.dto.AllowedUserDto
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
    onLogOut: (onFailure: () -> Unit) -> Unit = settingsViewModel!!::onLogOut,
    onCheckProfile: (onFailure: () -> Unit) -> Unit = settingsViewModel!!::onCheckProfile,
    allowedUser: State<AllowedUserDto> = settingsViewModel!!.userProfile.collectAsState(),
    authName: String?
) {
    var isOpenedAppearanceDialog by remember { mutableStateOf(false) }
    var isOpenedAboutUser by remember { mutableStateOf(false) }

    val aboutIntent = Intent(Intent.ACTION_VIEW, "https://github.com/unovil/QR-ID".toUri())

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
                "Settings ⚙️",
                modifier = Modifier.padding(horizontal = 20.dp),
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.headlineLarge
            )
            Spacer(modifier = Modifier.height(20.dp))

            SettingsItem(
                mainLabel = "Account information",
                subLabel = if (authName?.isNotEmpty() == true) "Hello, $authName! 👋" else ""
            ) {
                onCheckProfile {
                    Toast.makeText(context, "Something went wrong.", Toast.LENGTH_SHORT).show()
                }.also { isOpenedAboutUser = !isOpenedAboutUser }
            }

            SettingsItem(
                mainLabel = "Appearance",
                subLabel = "Currently set to: ${selectedAppearance.value}",
            ) { isOpenedAppearanceDialog = !isOpenedAppearanceDialog }

            SettingsItem(
                mainLabel = "About the QR-ID App",
                subLabel = "Made with 💖 through Android Studio and Git."
            ) { context.startActivity(aboutIntent) }

            SettingsItem(
                mainLabel = "Log out",
                subLabel = "End your current login session.",
                color = Color.Red,
            ) { onLogOut {
                Toast.makeText(context, "Something went wrong.", Toast.LENGTH_SHORT).show()
            }}
        }
    }

    if (isOpenedAboutUser) {
        AlertDialog(
            icon = { Icon(Icons.Default.Person, contentDescription = "About user") },
            title = { Text("About user") },
            text = {
                Column { 
                    Text("Name:", style = MaterialTheme.typography.labelMedium)
                    Text(allowedUser.value.name ?: "Unknown", style = MaterialTheme.typography.bodyLarge)
                    Spacer(modifier = Modifier.height(12.dp))
                    Text("Access Level:", style = MaterialTheme.typography.labelMedium)
                    Text(when (allowedUser.value.role) {
                        "FULL" -> "Access to entire school"
                        "CLASS" -> "Access to only your class"
                        "LEVEL" -> "Access to multiple classes on your level"
                        else -> "Unknown access"
                    }, style = MaterialTheme.typography.bodyLarge)
                    Spacer(modifier = Modifier.height(12.dp))
                    Text("Domain:", style = MaterialTheme.typography.labelMedium)
                    Text(allowedUser.value.domain, style = MaterialTheme.typography.bodyLarge)
                    Spacer(modifier = Modifier.height(12.dp))
                    Text("Domain ID:", style = MaterialTheme.typography.labelMedium)
                    Text(allowedUser.value.domainId, style = MaterialTheme.typography.bodyLarge)
                }
            },
            onDismissRequest = { isOpenedAboutUser = false },
            confirmButton = {
                TextButton(onClick = { isOpenedAboutUser = false }) {
                    Text("OK")
                }
            }
        )
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
            selectedAppearance.collectAsState(),
            authName = null
        )
    }
}