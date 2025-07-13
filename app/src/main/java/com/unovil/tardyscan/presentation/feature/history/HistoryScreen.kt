package com.unovil.tardyscan.presentation.feature.history

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.PreviewDynamicColors
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.unovil.tardyscan.domain.model.Attendance
import com.unovil.tardyscan.ui.theme.TardyScannerTheme
import kotlinx.datetime.Clock.System
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.format
import kotlinx.datetime.format.MonthNames
import kotlinx.datetime.toLocalDateTime

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreen(
    historyViewModel: HistoryViewModel? = hiltViewModel(),
    selectedDate: State<LocalDate> = historyViewModel!!.selectedDate.collectAsState(),
    loadAttendances: () -> Unit = historyViewModel!!::onLoadAttendances,
    onDateSelected: (LocalDate) -> Unit = historyViewModel!!::onChangeDate,
    attendances: State<List<Attendance>> = historyViewModel!!.attendances.collectAsState()
) {
    var showDatePicker by remember { mutableStateOf(false) }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(15.dp),
            horizontalAlignment = Alignment.Start,
        ) {
            Text(
                "History",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(20.dp)
            )

            TextButton(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 10.dp),
                onClick = { showDatePicker = true }
            ) { Text("Current date: ${selectedDate.value}") }

            Column(
                modifier = Modifier.verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(15.dp)
            ) {
                attendances.value.forEach {
                    HistoryItem(
                        it.name,
                        it.section,
                        it.studentId,
                        it.isPresent,
                        it.timestamp.toEpochMilliseconds()
                    )
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

@Composable
fun HistoryItem(name: String, section: String, lrn: Long, isPresent: Boolean, epochMilliseconds: Long) {
    val format = LocalDateTime.Format {
        monthName(MonthNames.ENGLISH_ABBREVIATED)
        chars(" ")
        dayOfMonth()
        chars(", ")
        year()

        chars(" ")

        hour()
        chars(":")
        minute()
        chars(":")
        second()
    }

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
        shape = RoundedCornerShape(6.dp),
        color = if (isPresent) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.errorContainer
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Row {
                Column(Modifier.weight(0.5f)) {
                    Text("Name", fontWeight = FontWeight.Bold)
                    Text(name)
                }
                Column(
                    modifier = Modifier.weight(0.5f),
                    horizontalAlignment = Alignment.End
                ) {
                    Text("LRN", fontWeight = FontWeight.Bold, textAlign = TextAlign.End)
                    Text(lrn.toString(), textAlign = TextAlign.End)
                }
            }
            Spacer(Modifier.padding(6.dp))
            Row {
                Column(Modifier.weight(0.5f)) {
                    Text("Section", fontWeight = FontWeight.Bold)
                    Text(section)
                }
                Column(
                    modifier = Modifier.weight(0.5f),
                    horizontalAlignment = Alignment.End
                ) {
                    Text("Timestamp", fontWeight = FontWeight.Bold, textAlign = TextAlign.End)
                    if (epochMilliseconds == 0L) {
                        Text("-", textAlign = TextAlign.End)
                    } else {
                        Text(
                            Instant.fromEpochMilliseconds(epochMilliseconds)
                                .toLocalDateTime(TimeZone.currentSystemDefault())
                                .format(format)
                                .toString(),
                            textAlign = TextAlign.End
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
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

@PreviewDynamicColors
@Composable
private fun PreviewHistoryItem() {
    val information = mapOf(
        "name" to "John Doe",
        "section" to "Section 1",
        "lrn" to 123456789123,
        "timestamp" to System.now().toEpochMilliseconds()
    )
    
    TardyScannerTheme {
        HistoryItem(
            information["name"] as String,
            information["section"] as String,
            information["lrn"] as Long,
            true,
            information["timestamp"] as Long,
        )
    }
}