package com.unovil.tardyscan.presentation.feature.history

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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.unovil.tardyscan.R

@Composable
fun AttendanceItem(attendanceUi: AttendanceUiModel) {
    val color = when (attendanceUi.presence) {
        Presence.PRESENT -> MaterialTheme.colorScheme.primaryContainer
        Presence.ABSENT -> MaterialTheme.colorScheme.errorContainer
        Presence.LATE -> Color(0xFFA6A613)
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
                    Text(stringResource(R.string.history_name), fontWeight = FontWeight.Bold)
                    Text(attendanceUi.name)
                }
                Column(
                    modifier = Modifier.weight(0.5f),
                    horizontalAlignment = Alignment.End
                ) {
                    Text("LRN", fontWeight = FontWeight.Bold, textAlign = TextAlign.End)
                    Text(attendanceUi.id.toString(), textAlign = TextAlign.End)
                }
            }
            Spacer(Modifier.padding(6.dp))
            Row {
                Column(Modifier.weight(0.5f)) {
                    Text(stringResource(R.string.history_section), fontWeight = FontWeight.Bold)
                    Text("${attendanceUi.level} - ${attendanceUi.section}")
                }
                Column(
                    modifier = Modifier.weight(0.5f),
                    horizontalAlignment = Alignment.End
                ) {
                    Text(stringResource(R.string.history_timestamp), fontWeight = FontWeight.Bold, textAlign = TextAlign.End)
                    if (attendanceUi.presence == Presence.ABSENT) {
                        Text(stringResource(R.string.history_not_yet_scanned), fontStyle = FontStyle.Italic, textAlign = TextAlign.End)
                    } else {
                        Text(
                            attendanceUi.displayTimestamp,
                            textAlign = TextAlign.End
                        )
                    }
                }
            }
        }
    }
}