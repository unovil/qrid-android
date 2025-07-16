package com.unovil.tardyscan.presentation.feature.history

import android.util.Log
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.PreviewDynamicColors
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.unovil.tardyscan.ui.theme.TardyScannerTheme
import kotlinx.datetime.Clock.System
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn
import kotlinx.datetime.toLocalDateTime

@ExperimentalMaterial3Api
@Composable
fun HistoryScreen(
    historyViewModel: HistoryViewModel? = hiltViewModel(),
    selectedTimestamp: State<Instant> = historyViewModel!!.selectedTimestamp.collectAsState(),
    attendanceFilters: List<String> = historyViewModel!!.attendanceFilterOptions,
    selectedFilter: State<String> = historyViewModel!!.selectedFilter.collectAsState(),
    onChangeFilter: (String) -> Unit = historyViewModel!!::onChangeFilter,
    isAttendancesLoaded: State<Boolean> = historyViewModel!!.isAttendancesLoaded.collectAsState(),
    loadAttendances: () -> Unit = historyViewModel!!::onLoadAttendances,
    onDateSelected: (LocalDate) -> Unit = historyViewModel!!::onChangeDate,
    attendances: State<List<AttendanceUiModel>> = historyViewModel!!.filteredUiAttendances.collectAsState()
) {
    var showDatePicker by remember { mutableStateOf(false) }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(15.dp, 15.dp, 15.dp, 0.dp),
            horizontalAlignment = Alignment.Start,
        ) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(20.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "History",
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.weight(0.5f)
                )

                OutlinedButton (
                    modifier = Modifier
                        .weight(0.5f),
                    shape = MaterialTheme.shapes.medium,
                    onClick = { showDatePicker = true }
                ) {
                    Icon(Icons.Default.CalendarMonth, "Choose date")
                    Spacer(modifier = Modifier.width(5.dp))
                    Text(
                        "${selectedTimestamp.value.toLocalDateTime(TimeZone.currentSystemDefault()).date}",
                        textAlign = TextAlign.End
                    )
                }
            }

            SingleChoiceSegmentedButtonRow(
                modifier = Modifier.padding(10.dp)
            ) {
                attendanceFilters.forEachIndexed { index, label ->
                    SegmentedButton(
                        shape = SegmentedButtonDefaults.itemShape(
                            index = index,
                            count = attendanceFilters.size,
                            baseShape = MaterialTheme.shapes.small
                        ),
                        onClick = {
                            Log.d("HistoryScreen", "Selected filter is now: $label")
                            onChangeFilter(label)
                                  },
                        selected = selectedFilter.value == label,
                        label = { Text(label, style = MaterialTheme.typography.labelMedium) }
                    )
                }
            }

            Crossfade(targetState = isAttendancesLoaded.value) { isAttendancesLoaded ->
                if (isAttendancesLoaded) {
                    LazyColumn(
                        contentPadding = PaddingValues(bottom = 15.dp),
                        verticalArrangement = Arrangement.spacedBy(15.dp)
                    ) {
                        items(attendances.value.size) { index ->
                            HistoryItem(attendances.value[index])
                        }
                    }
                } else {
                    Column(
                        modifier = Modifier.fillMaxSize().padding(vertical = 16.dp, horizontal = 48.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text("Cannot fetch attendance records from the server.", textAlign = TextAlign.Center)
                        Spacer(Modifier.height(16.dp))
                        Button(onClick = loadAttendances) {
                            Text("Refresh!")
                        }
                    }
                }
            }
        }
    }

    LaunchedEffect(Unit) {
        loadAttendances()
    }

    if (showDatePicker) {
        DatePickerModal({
            onDateSelected(
                Instant
                    .fromEpochMilliseconds(it)
                    .toLocalDateTime(TimeZone.currentSystemDefault())
                    .date
            )
        }) { showDatePicker = false }
    }

    /**
     * We need the ff data:
     * 1. name
     * 2. section
     * 3. timestamp
     */
}

@ExperimentalMaterial3Api
@Composable
fun DatePickerModal(
    onDateSelected: (Long) -> Unit,
    onDismiss: () -> Unit
) {
    val datePickerState = rememberDatePickerState()

    DatePickerDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(
                enabled = (datePickerState.selectedDateMillis != null),
                onClick = {
                    onDateSelected(datePickerState.selectedDateMillis!!)
                    onDismiss()
                }
            ) {
                Text("OK")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    ) {
        DatePicker(state = datePickerState)
    }
}

@ExperimentalMaterial3Api
@PreviewLightDark
@Composable
private fun PreviewHistoryScreen() {
    val attendanceFilterOptions = listOf("Present", "Absent", "Late", "All")
    val selectedDate = remember { mutableStateOf(System.now()) }
    val filter = remember { mutableStateOf(attendanceFilterOptions[0]) }
    val attendances = remember { mutableStateOf<List<AttendanceUiModel>>(listOf()) }

    TardyScannerTheme {
        HistoryScreen(
            historyViewModel = null,
            selectedTimestamp = selectedDate,
            onChangeFilter = { filter.value = it },
            selectedFilter = filter,
            loadAttendances = { },
            attendanceFilters = attendanceFilterOptions,
            onDateSelected = { selectedDate.value = it.atStartOfDayIn(TimeZone.currentSystemDefault()) },
            attendances = attendances
        )
    }
}

@PreviewDynamicColors
@Composable
private fun PreviewHistoryItem() {
    /*val information = mapOf(
        "name" to "John Doe",
        "section" to "Section 1",
        "lrn" to 123456789123,
        "timestamp" to System.now().toEpochMilliseconds()
    )*/
    
    TardyScannerTheme {
        /*HistoryItem(
            information["name"] as String,
            information["section"] as String,
            information["lrn"] as Long,
            true,
            information["timestamp"] as Long,
        )*/
    }
}