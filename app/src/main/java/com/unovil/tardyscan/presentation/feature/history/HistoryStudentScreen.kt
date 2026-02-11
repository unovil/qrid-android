package com.unovil.tardyscan.presentation.feature.history

import android.Manifest
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material3.Button
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
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
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionStatus
import com.google.accompanist.permissions.rememberPermissionState
import com.kizitonwose.calendar.compose.HorizontalCalendar
import com.kizitonwose.calendar.compose.rememberCalendarState
import com.kizitonwose.calendar.core.CalendarDay
import com.kizitonwose.calendar.core.DayPosition
import com.kizitonwose.calendar.core.OutDateStyle
import com.kizitonwose.calendar.core.daysOfWeek
import com.kizitonwose.calendar.core.minusMonths
import com.kizitonwose.calendar.core.plusMonths
import com.unovil.tardyscan.R
import kotlinx.coroutines.launch
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate
import kotlinx.datetime.YearMonth
import kotlinx.datetime.toJavaDayOfWeek
import java.time.format.TextStyle
import java.util.Locale
import kotlin.time.ExperimentalTime

@Composable
@OptIn(ExperimentalPermissionsApi::class, ExperimentalTime::class)
fun HistoryStudentScreen(
    historyStudentViewModel: HistoryStudentViewModel? = hiltViewModel(),
    selectedMonth: State<YearMonth> = historyStudentViewModel!!.selectedMonth.collectAsState(),
    attendanceFilters: List<String> = historyStudentViewModel!!.attendanceFilterOptions,
    selectedFilter: State<String> = historyStudentViewModel!!.selectedFilter.collectAsState(),
    onChangeFilter: (String) -> Unit = historyStudentViewModel!!::onChangeFilter,
    attendanceViews: List<String> = historyStudentViewModel!!.attendanceViewOptions,
    selectedAttendanceView: State<String> = historyStudentViewModel!!.selectedAttendanceView.collectAsState(),
    onChangeAttendanceView: (String) -> Unit = historyStudentViewModel!!::onChangeAttendanceView,
    isAttendancesLoaded: State<Boolean> = historyStudentViewModel!!.isAttendancesLoaded.collectAsState(),
    loadAttendances: () -> Unit = historyStudentViewModel!!::onLoadAttendances,
    onMonthSelected: (YearMonth) -> Unit = historyStudentViewModel!!::onChangeMonth,
    attendances: State<List<AttendanceDayUiModel>> = historyStudentViewModel!!.filteredUiAttendances.collectAsState(),
    calendarPresences: State<Map<LocalDate, Presence>> = historyStudentViewModel!!.calendarPresences.collectAsState()
) {
    val coroutineScope = rememberCoroutineScope()
    val resources = LocalResources.current
    val context = LocalContext.current
    val startMonth = remember { selectedMonth.value.minusMonths(48) } // Adjust as needed
    val endMonth = remember { selectedMonth.value.plusMonths(48) } // Adjust as needed
    val daysOfWeek = remember { daysOfWeek() } // Available from the library
    var selectedDate by remember { mutableStateOf<LocalDate?>(null) }

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        val notificationPermissionState = rememberPermissionState(
            Manifest.permission.POST_NOTIFICATIONS
        )

        LaunchedEffect(notificationPermissionState) {
            if (notificationPermissionState.status is PermissionStatus.Denied) {
                notificationPermissionState.launchPermissionRequest()
            }
        }
    }

    val state = rememberCalendarState(
        startMonth = startMonth,
        endMonth = endMonth,
        firstVisibleMonth = selectedMonth.value,
        firstDayOfWeek = daysOfWeek.first(),
        outDateStyle = OutDateStyle.EndOfGrid
    )

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
                        onClick = {
                            if (selectedAttendanceView.value == attendanceViews[0])
                                onChangeAttendanceView(attendanceViews[1])
                            else
                                onChangeAttendanceView(attendanceViews[0])
                        }
                    ) {
                        Icon(Icons.Default.CalendarMonth, "Choose presence")
                        Spacer(modifier = Modifier.width(5.dp))
                        Text(
                            selectedAttendanceView.value,
                            textAlign = TextAlign.End
                        )
                    }
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth().padding(8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                FilledIconButton(
                    onClick = {
                        if (selectedMonth.value.minusMonths(1) < startMonth) return@FilledIconButton
                        coroutineScope.launch {
                            onMonthSelected(selectedMonth.value.minusMonths(1))
                            state.scrollToMonth(selectedMonth.value)
                        }
                        selectedDate = null
                    },
                    shape = MaterialTheme.shapes.small
                ) {
                    Icon(
                        Icons.AutoMirrored.Filled.ArrowBack,
                        "Previous Month"
                    )
                }

                Text(
                    "${selectedMonth.value.month.name} ${selectedMonth.value.year}",
                    fontWeight = FontWeight.Bold
                )

                FilledIconButton(
                    onClick = {
                        if (selectedMonth.value.plusMonths(1) > endMonth) return@FilledIconButton
                        coroutineScope.launch {
                            onMonthSelected(selectedMonth.value.plusMonths(1))
                            state.scrollToMonth(selectedMonth.value)
                        }
                        selectedDate = null
                    },
                    shape = MaterialTheme.shapes.small
                ) {
                    Icon(
                        Icons.AutoMirrored.Filled.ArrowForward,
                        "Next Month"
                    )
                }
            }

            if (selectedAttendanceView.value == attendanceViews[0]) { // records
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
            else if (selectedAttendanceView.value == attendanceViews[1]) { // calendar
                Column {
                    HorizontalCalendar(
                        modifier = Modifier.fillMaxWidth().padding(8.dp),
                        state = state,
                        userScrollEnabled = false,
                        dayContent = { it ->
                            Day(it, calendarPresences.value[it.date]) {
                            selectedDate = it.date
                        } },
                        monthHeader = { DaysOfWeekTitle(daysOfWeek = daysOfWeek) }
                    )
                    if (selectedDate != null) {
                        attendances.value.find { it.date == selectedDate }?.let {
                            Box(
                                modifier = Modifier.fillMaxWidth().padding(8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                AttendanceItem(it)
                            }
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
                resources.getString(R.string.history_filter_on_time) -> Toast.makeText(context,"$size record${if (size != 1) "s" else ""} on time", Toast.LENGTH_SHORT).show()
                resources.getString(R.string.history_filter_absent) -> Toast.makeText(context,"$size absent record${if (size != 1) "s" else ""}", Toast.LENGTH_SHORT).show()
                resources.getString(R.string.history_filter_late) -> Toast.makeText(context,"$size late record${if (size != 1) "s" else ""}", Toast.LENGTH_SHORT).show()
                resources.getString(R.string.history_filter_all) -> Toast.makeText(context,"$size total record${if (size != 1) "s" else ""}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    LaunchedEffect(Unit) {
        loadAttendances()
    }
}

@Composable
fun Day(day: CalendarDay, presence: Presence?, onClick: (CalendarDay) -> Unit) {
    val color = when (presence) {
        Presence.PRESENT -> MaterialTheme.colorScheme.primaryContainer
        Presence.ABSENT -> MaterialTheme.colorScheme.errorContainer
        Presence.LATE -> Color(0xFFA6A613)
        else -> Color.Unspecified
    }

    Box(
        modifier = Modifier
            .aspectRatio(1f) // This is important for square sizing
            .clickable(
                enabled = presence != null,
                onClick = { onClick(day) }
            ),
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