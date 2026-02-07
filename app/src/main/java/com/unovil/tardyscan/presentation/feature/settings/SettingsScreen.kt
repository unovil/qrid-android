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
import androidx.compose.ui.platform.LocalResources
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.unovil.tardyscan.R
import com.unovil.tardyscan.domain.model.User
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
    allowedUser: State<User?> = settingsViewModel!!.userProfile.collectAsState(),
    authName: String?
) {
    var isOpenedAppearanceDialog by remember { mutableStateOf(false) }
    var isOpenedAboutUser by remember { mutableStateOf(false) }

    val aboutIntent = Intent(Intent.ACTION_VIEW, "https://github.com/unovil/QR-ID".toUri())

    val context = LocalContext.current
    val resources = LocalResources.current

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
                stringResource(R.string.settings_title),
                modifier = Modifier.padding(horizontal = 20.dp),
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.headlineLarge
            )
            Spacer(modifier = Modifier.height(20.dp))

            SettingsItem(
                mainLabel = stringResource(R.string.settings_account_information),
                subLabel = if (authName?.isNotEmpty() == true) stringResource(
                    R.string.settings_account_information_sub,
                    authName
                ) else ""
            ) {
                onCheckProfile {
                    Toast.makeText(context, resources.getString(R.string.error_unknown), Toast.LENGTH_SHORT).show()
                }.also { isOpenedAboutUser = !isOpenedAboutUser }
            }

            SettingsItem(
                mainLabel = stringResource(R.string.settings_appearance),
                subLabel = stringResource(
                    R.string.settings_appearance_sub,
                    selectedAppearance.value
                ),
            ) { isOpenedAppearanceDialog = !isOpenedAppearanceDialog }

            SettingsItem(
                mainLabel = stringResource(R.string.settings_about),
                subLabel = stringResource(R.string.settings_about_sub)
            ) { context.startActivity(aboutIntent) }

            SettingsItem(
                mainLabel = stringResource(R.string.settings_logout),
                subLabel = stringResource(R.string.settings_logout_sub),
                color = Color.Red,
            ) { onLogOut {
                Toast.makeText(context, resources.getString(R.string.error_unknown), Toast.LENGTH_SHORT).show()
            }}
        }
    }

    if (isOpenedAboutUser) {
        AlertDialog(
            icon = { Icon(Icons.Default.Person, contentDescription = stringResource(R.string.content_desc_about_user)) },
            title = { Text(stringResource(R.string.settings_profile_title)) },
            text = {
                Column {
                    when (allowedUser.value) {
                        is User.Administrator -> {
                            val admin = (allowedUser.value as User.Administrator).admin

                            Text(stringResource(R.string.settings_profile_name), style = MaterialTheme.typography.labelMedium)
                            Text(admin.name ?: stringResource(R.string.settings_profile_name_error), style = MaterialTheme.typography.bodyLarge)

                            Spacer(modifier = Modifier.height(12.dp))

                            Text(stringResource(R.string.settings_profile_access_level), style = MaterialTheme.typography.labelMedium)
                            Text(when (admin.role) {
                                "FULL" -> stringResource(R.string.settings_profile_access_level_full)
                                "CLASS" -> stringResource(R.string.settings_profile_access_level_class)
                                "LEVEL" -> stringResource(R.string.settings_profile_access_level_level)
                                else -> stringResource(R.string.settings_profile_access_level_error)
                            }, style = MaterialTheme.typography.bodyLarge)

                            Spacer(modifier = Modifier.height(12.dp))

                            Text(stringResource(R.string.settings_profile_domain), style = MaterialTheme.typography.labelMedium)
                            Text(admin.domain, style = MaterialTheme.typography.bodyLarge)

                            Spacer(modifier = Modifier.height(12.dp))

                            Text(stringResource(R.string.settings_profile_domain_id), style = MaterialTheme.typography.labelMedium)
                            Text(admin.domainId, style = MaterialTheme.typography.bodyLarge)
                        }
                        is User.Student -> {
                            val student = (allowedUser.value as User.Student).student
                            val name = "${student.lastName}, ${student.firstName} ${student.middleName}"
                            val section = "${student.level} - ${student.section}"

                            Text(stringResource(R.string.settings_profile_name), style = MaterialTheme.typography.labelMedium)
                            Text(name, style = MaterialTheme.typography.bodyLarge)

                            Spacer(modifier = Modifier.height(12.dp))

                            Text("Section:", style = MaterialTheme.typography.labelMedium)
                            Text(section, style = MaterialTheme.typography.bodyLarge)

                            Spacer(modifier = Modifier.height(12.dp))

                            Text("School:", style = MaterialTheme.typography.labelMedium)
                            Text(student.school, style = MaterialTheme.typography.bodyLarge)

                            Spacer(modifier = Modifier.height(12.dp))

                            Text("LRN:", style = MaterialTheme.typography.labelMedium)
                            Text(student.id.toString(), style = MaterialTheme.typography.bodyLarge)
                        }
                        null -> { }
                    }
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
            icon = { Icon(Icons.Default.Palette, contentDescription = stringResource(R.string.content_desc_appearance)) },
            title = { Text(stringResource(R.string.settings_appearance_title)) },
            text = {
                Column(
                    modifier = Modifier.selectableGroup(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    appearanceList.forEach { text ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .selectable(
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
                    Text(stringResource(R.string.settings_appearance_confirm))
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    isOpenedAppearanceDialog = false
                    onCancelAppearance()
                }) {
                    Text(stringResource(R.string.settings_appearance_cancel))
                }
            }
        )
    }
}

@Composable
@Preview
fun SettingsScreenPreview() {
    val appearanceList = listOf(stringResource(R.string.settings_appearance_option_light),
        stringResource(R.string.settings_appearance_option_dark),
        stringResource(R.string.settings_appearance_option_system)
    )
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