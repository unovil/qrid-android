package com.unovil.tardyscan.presentation.feature.history

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn
import kotlinx.datetime.format
import kotlinx.datetime.format.MonthNames
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Duration

@Composable
fun HistoryItem(selectedDate: LocalDate, name: String, section: String, lrn: Long, isPresent: Boolean, epochMilliseconds: Long) {
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

    val startOfLate = selectedDate
        .atStartOfDayIn(TimeZone.currentSystemDefault())
        .plus(Duration.parse("7h"))

    val attendanceInstant = Instant.fromEpochMilliseconds(epochMilliseconds)

    if (lrn == 535317030410L) {
        Log.d("HistoryItem", "attendanceInstant: $attendanceInstant, startOfLate: $startOfLate")
    }

    val color = if (attendanceInstant > startOfLate) {
        Color(0xFFA6A613)
    } else if (!isPresent) {
        MaterialTheme.colorScheme.errorContainer
    } else {
        MaterialTheme.colorScheme.primaryContainer
    }

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
        shape = RoundedCornerShape(6.dp),
        color = color
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
                        Text("Not yet scanned", fontStyle = FontStyle.Italic, textAlign = TextAlign.End)
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