package com.unovil.tardyscan.presentation.feature.history

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.kizitonwose.calendar.compose.HorizontalCalendar
import com.kizitonwose.calendar.compose.rememberCalendarState
import com.kizitonwose.calendar.core.CalendarDay
import com.kizitonwose.calendar.core.daysOfWeek
import com.kizitonwose.calendar.core.firstDayOfWeekFromLocale
import com.kizitonwose.calendar.core.minusMonths
import com.kizitonwose.calendar.core.now
import com.kizitonwose.calendar.core.plusMonths
import kotlinx.coroutines.launch
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.YearMonth
import kotlinx.datetime.toJavaDayOfWeek
import java.time.format.TextStyle
import java.util.Locale
import kotlin.time.ExperimentalTime

@Composable
@ExperimentalTime
fun HistoryStudentScreen(
    historyStudentViewModel: HistoryStudentViewModel? = hiltViewModel()
) {
    val coroutineScope = rememberCoroutineScope()
    var currentMonth by remember { mutableStateOf(YearMonth.now()) }
    val startMonth = remember { currentMonth.minusMonths(48) } // Adjust as needed
    val endMonth = remember { currentMonth.plusMonths(48) } // Adjust as needed
    val daysOfWeek = remember { daysOfWeek() } // Available from the library

    val state = rememberCalendarState(
        startMonth = startMonth,
        endMonth = endMonth,
        firstVisibleMonth = currentMonth,
        firstDayOfWeek = daysOfWeek.first()
    )

    Column {
        HorizontalCalendar(
            state = state,
            userScrollEnabled = false,
            dayContent = { Day(it) },
            monthHeader = {
                DaysOfWeekTitle(daysOfWeek = daysOfWeek) // Use the title as month header
            }
        )

        Button(onClick = {
            coroutineScope.launch {
                currentMonth = currentMonth.plusMonths(1)
                state.animateScrollToMonth(currentMonth)
            }
        } ) {
            Text("Next Month")
        }

        Button(onClick = {
            coroutineScope.launch {
                currentMonth = currentMonth.minusMonths(1)
                state.animateScrollToMonth(currentMonth)
            }
        } ) {
            Text("Previous Month")
        }
    }

}

@Composable
fun Day(day: CalendarDay) {
    Box(
        modifier = Modifier
            .aspectRatio(1f), // This is important for square sizing!
        contentAlignment = Alignment.Center
    ) {
        Text(text = day.date.day.toString())
    }
}

@Composable
fun DaysOfWeekTitle(daysOfWeek: List<DayOfWeek>) {
    Row(modifier = Modifier.fillMaxWidth()) {
        for (dayOfWeek in daysOfWeek) {
            Text(
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Center,
                text = dayOfWeek.toJavaDayOfWeek().getDisplayName(TextStyle.SHORT, Locale.getDefault()),
            )
        }
    }
}