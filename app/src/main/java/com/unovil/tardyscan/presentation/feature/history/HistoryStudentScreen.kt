package com.unovil.tardyscan.presentation.feature.history

import android.widget.Toast
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalResources
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.kizitonwose.calendar.compose.HorizontalCalendar
import com.kizitonwose.calendar.compose.rememberCalendarState
import com.kizitonwose.calendar.core.CalendarDay
import com.kizitonwose.calendar.core.DayPosition
import com.kizitonwose.calendar.core.daysOfWeek
import com.kizitonwose.calendar.core.minusMonths
import com.kizitonwose.calendar.core.plusMonths
import com.unovil.tardyscan.R
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
    historyStudentViewModel: HistoryStudentViewModel? = hiltViewModel(),
    selectedMonth: State<YearMonth> = historyStudentViewModel!!.selectedMonth.collectAsState(),
    attendanceFilters: List<String> = historyStudentViewModel!!.attendanceFilterOptions,
    selectedFilter: State<String> = historyStudentViewModel!!.selectedFilter.collectAsState(),
    onChangeFilter: (String) -> Unit = historyStudentViewModel!!::onChangeFilter,
    isAttendancesLoaded: State<Boolean> = historyStudentViewModel!!.isAttendancesLoaded.collectAsState(),
    loadAttendances: () -> Unit = historyStudentViewModel!!::onLoadAttendances,
    onMonthSelected: (YearMonth) -> Unit = historyStudentViewModel!!::onChangeMonth,
    attendances: State<List<AttendanceUiModel>> = historyStudentViewModel!!.filteredUiAttendances.collectAsState(),
    calendarUiAttendances: State<List<Presence>> = historyStudentViewModel!!.calendarPresences.collectAsState()
) {
    val coroutineScope = rememberCoroutineScope()
    val resources = LocalResources.current
    val context = LocalContext.current
    val startMonth = remember { selectedMonth.value.minusMonths(48) } // Adjust as needed
    val endMonth = remember { selectedMonth.value.plusMonths(48) } // Adjust as needed
    val daysOfWeek = remember { daysOfWeek() } // Available from the library

    val state = rememberCalendarState(
        startMonth = startMonth,
        endMonth = endMonth,
        firstVisibleMonth = selectedMonth.value,
        firstDayOfWeek = daysOfWeek.first()
    )

    var showPresenceDropdown by remember { mutableStateOf(false) }

    val attendanceByDate = remember(calendarUiAttendances.value) {
        buildMap {
            var index = 0

            selectedMonth.value.days.forEach { day ->
                if (
                    day.dayOfWeek !in setOf(DayOfWeek.SATURDAY, DayOfWeek.SUNDAY)
                ) {
                    if (index < calendarUiAttendances.value.size) {
                        put(day, calendarUiAttendances.value[index])
                        index++
                    }
                }
            }
        }
    }


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
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    stringResource(R.string.history_title),
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.weight(0.5f)
                )

                Box(
                    modifier = Modifier.weight(0.5f),
                    contentAlignment = Alignment.CenterEnd,
                    propagateMinConstraints = true
                ) {
                    OutlinedButton (
                        // modifier = Modifier.weight(0.5f),
                        shape = MaterialTheme.shapes.medium,
                        onClick = { showPresenceDropdown = !showPresenceDropdown }
                    ) {
                        Icon(Icons.Default.CalendarMonth, "Choose presence")
                        Spacer(modifier = Modifier.width(5.dp))
                        Text(
                            selectedFilter.value,
                            textAlign = TextAlign.End
                        )
                    }

                    DropdownMenu(
                        // modifier = Modifier.width(IntrinsicSize.Max).,
                        expanded = showPresenceDropdown,
                        onDismissRequest = { showPresenceDropdown = false }
                    ) {
                        attendanceFilters.forEach {
                            DropdownMenuItem(
                                text = { Text(it) },
                                trailingIcon = {
                                    if (selectedFilter.value == it) Icon(
                                        Icons.Default.Check,
                                        "Selected"
                                    )
                                },
                                onClick = {
                                    onChangeFilter(it)
                                    showPresenceDropdown = false
                                }
                            )
                        }
                    }
                }
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .border(
                        color = Color.Black,
                        width = 1.dp,
                        shape = RoundedCornerShape(8.dp)
                    )
            ) {
                HorizontalCalendar(
                    state = state,
                    userScrollEnabled = false,
                    dayContent = {
                        if (it.date.dayOfWeek in arrayOf(DayOfWeek.SATURDAY, DayOfWeek.SUNDAY) || it.position != DayPosition.MonthDate) {
                            Day(it, null)
                        } else {
                            Day(it, attendanceByDate[it.date])
                        }
                    },
                    monthHeader = {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            FilledIconButton(
                                onClick = {
                                    coroutineScope.launch {
                                        onMonthSelected(selectedMonth.value.minusMonths(1))
                                        state.animateScrollToMonth(selectedMonth.value)
                                    }
                                },
                                shape = MaterialTheme.shapes.small
                            ) {
                                Icon(
                                    Icons.AutoMirrored.Filled.ArrowBack,
                                    "Previous Month"
                                )
                            }

                            Text(
                                "${it.yearMonth.month.name} ${it.yearMonth.year}",
                                fontWeight = FontWeight.Bold
                            )

                            FilledIconButton(
                                onClick = {
                                    coroutineScope.launch {
                                        onMonthSelected(selectedMonth.value.plusMonths(1))
                                        state.scrollToMonth(selectedMonth.value)
                                    }
                                },
                                shape = MaterialTheme.shapes.small
                            ) {
                                Icon(
                                    Icons.AutoMirrored.Filled.ArrowForward,
                                    "Next Month"
                                )
                            }
                        }


                        DaysOfWeekTitle(daysOfWeek = daysOfWeek) // Use the title as month header
                    }
                )
            }

            Crossfade(targetState = isAttendancesLoaded.value) { isAttendancesLoaded ->
                if (isAttendancesLoaded && attendances.value.isNotEmpty()) {
                    LazyColumn(
                        contentPadding = PaddingValues(bottom = 15.dp),
                        verticalArrangement = Arrangement.spacedBy(15.dp)
                    ) {
                        items(attendances.value.size) { index ->
                            AttendanceItem(attendances.value[index])
                        }
                    }
                } else if (isAttendancesLoaded && attendances.value.isEmpty()) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(vertical = 16.dp, horizontal = 48.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(stringResource(R.string.history_no_attendance_records_exist), textAlign = TextAlign.Center)
                    }
                } else {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(vertical = 16.dp, horizontal = 48.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(stringResource(R.string.history_cannot_fetch_attendance), textAlign = TextAlign.Center)
                        Spacer(Modifier.height(16.dp))
                        Button(onClick = loadAttendances) {
                            Text(stringResource(R.string.history_refresh))
                        }
                    }
                }
            }
        }
    }

    // hardcode muna, to fix later
    LaunchedEffect(isAttendancesLoaded.value, selectedFilter.value) {
        val size = attendances.value.size
        if (isAttendancesLoaded.value) {
            when (selectedFilter.value) {
                resources.getString(R.string.history_filter_on_time) -> Toast.makeText(context,
                    resources.getString(
                        R.string.history_toast_student_on_time,
                        size,
                        if (size != 1) "s" else ""
                    ), Toast.LENGTH_SHORT).show()
                resources.getString(R.string.history_filter_absent) -> Toast.makeText(context,
                    resources.getString(
                        R.string.history_toast_student_absent,
                        size,
                        if (size != 1) "s" else ""
                    ), Toast.LENGTH_SHORT).show()
                resources.getString(R.string.history_filter_late) -> Toast.makeText(context,
                    resources.getString(
                        R.string.history_toast_student_late,
                        size,
                        if (size != 1) "s" else ""
                    ), Toast.LENGTH_SHORT).show()
                resources.getString(R.string.history_filter_all) -> Toast.makeText(context,
                    resources.getString(
                        R.string.history_toast_student_all, size, if (size != 1) "s" else ""
                    ), Toast.LENGTH_SHORT).show()
            }
        }
    }

    LaunchedEffect(Unit) {
        loadAttendances()
    }
}

@Composable
fun Day(day: CalendarDay, presence: Presence?) {
    val color = when (presence) {
        Presence.PRESENT -> MaterialTheme.colorScheme.primaryContainer
        Presence.ABSENT -> MaterialTheme.colorScheme.errorContainer
        Presence.LATE -> Color(0xFFA6A613)
        else -> Color.Unspecified
    }

    Box(
        modifier = Modifier.aspectRatio(1f), // This is important for square sizing
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize(0.8f)
                .clip(CircleShape)
                .background(color),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = day.date.day.toString(),
                color = if (day.position == DayPosition.MonthDate) Color.Unspecified else Color.Gray
            )
        }
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