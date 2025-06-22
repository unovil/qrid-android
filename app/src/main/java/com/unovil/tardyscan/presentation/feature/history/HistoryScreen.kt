package com.unovil.tardyscan.presentation.feature.history

import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.unovil.tardyscan.ui.theme.TardyScannerTheme
import kotlinx.datetime.Instant

@Composable
fun HistoryScreen(
    historyViewModel: HistoryViewModel? = hiltViewModel(),
    navController: NavController
) {
    TardyScannerTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            Column (
                modifier = Modifier
                    .fillMaxSize()
                    .padding(15.dp)
                    .scrollable(rememberScrollState(), Orientation.Vertical),
                horizontalAlignment = Alignment.Start,
                verticalArrangement = Arrangement.spacedBy(15.dp)
            ) {
                Text(
                    "History",
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(20.dp)
                )

                HistoryItem("name", "section", 123456789123, Instant.parse("2021-01-01T00:00:00Z"))
            }
        }
    }

    LaunchedEffect(Unit) {
        historyViewModel?.testFunction()
    }

    /**
     * We need the ff data:
     * 1. name
     * 2. section
     * 3. timestamp
     */
}

@Composable
fun HistoryItem(name: String, section: String, lrn: Long, timestamp: Instant) {
    Surface(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp),
        shape = RoundedCornerShape(6.dp),
        color = MaterialTheme.colorScheme.surfaceTint
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
                    Text("LRN", fontWeight = FontWeight.Bold)
                    Text(lrn.toString())
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
                    Text("Timestamp", fontWeight = FontWeight.Bold)
                    Text(timestamp.toString())
                }
            }
        }
    }
}

@Preview
@Composable
private fun PreviewHistoryScreen() {
    HistoryScreen(null, rememberNavController())
}

@Preview
@Composable
private fun PreviewHistoryItem() {
    val information = mapOf(
        "name" to "John Doe",
        "section" to "Section 1",
        "lrn" to 123456789123,
        "timestamp" to Instant.parse("2021-01-01T00:00:00Z")
    )
    HistoryItem(information["name"] as String, information["section"] as String, information["lrn"] as Long, information["timestamp"] as Instant)
}