package com.unovil.tardyscan.presentation.feature.history

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kizitonwose.calendar.core.now
import com.unovil.tardyscan.R
import com.unovil.tardyscan.domain.model.Attendance
import com.unovil.tardyscan.domain.usecase.GetAttendancesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.YearMonth
import kotlinx.datetime.atStartOfDayIn
import kotlinx.datetime.format
import kotlinx.datetime.format.DayOfWeekNames
import kotlinx.datetime.format.MonthNames
import kotlinx.datetime.format.Padding
import kotlinx.datetime.toLocalDateTime
import javax.inject.Inject
import kotlin.time.Clock
import kotlin.time.Duration
import kotlin.time.ExperimentalTime

@HiltViewModel
@ExperimentalTime
class HistoryStudentViewModel @Inject constructor(
    private val getAttendancesUseCase: GetAttendancesUseCase,
    @ApplicationContext val context: Context
) : ViewModel() {

    val attendanceFilterOptions = listOf(
        context.getString(R.string.history_filter_all),
        context.getString(R.string.history_filter_on_time),
        context.getString(R.string.history_filter_absent),
        context.getString(R.string.history_filter_late),
    )

    val attendanceViewOptions = listOf("Records", "Calendar")

    private val dateFormat = LocalDate.Format {
        dayOfWeek(DayOfWeekNames.ENGLISH_ABBREVIATED)
        chars(", ")
        monthName(MonthNames.ENGLISH_FULL)
        chars(" ")
        day(Padding.NONE)
    }

    private val timestampFormat = LocalDateTime.Format {
        hour()
        chars(":")
        minute()
        chars(":")
        second()
    }

    private var attendances = listOf<Attendance>()
    private val _isAttendancesLoaded = MutableStateFlow(true)
    val isAttendancesLoaded = _isAttendancesLoaded.asStateFlow()

    private val _filteredUiAttendances = MutableStateFlow<List<AttendanceDayUiModel>>(emptyList())
    val filteredUiAttendances = _filteredUiAttendances.asStateFlow()

    private val _calendarPresences = MutableStateFlow<Map<LocalDate, Presence>>(emptyMap())
    val calendarPresences = _calendarPresences.asStateFlow()

    private val _selectedMonth = MutableStateFlow(YearMonth.now())
    val selectedMonth = _selectedMonth.asStateFlow()

    private val _selectedFilter = MutableStateFlow(attendanceFilterOptions[0])
    val selectedFilter = _selectedFilter.asStateFlow()

    private val _selectedAttendanceView = MutableStateFlow(attendanceViewOptions[0])
    val selectedAttendanceView = _selectedAttendanceView.asStateFlow()

    fun onLoadAttendances() {
        _calendarPresences.value = emptyMap()

        viewModelScope.launch {
            when (val result = getAttendancesUseCase.execute(GetAttendancesUseCase.Input.Student(
                _selectedMonth.value
            ))) {
                is GetAttendancesUseCase.Output.Success -> {
                    attendances = result.attendanceList.sortedWith(comparator =
                        compareBy { it.timestamp }
                    )

                    val today = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date

                    _selectedMonth.value.days.forEach { day ->
                        if (day > today) return@forEach // don't add attendances after today
                        if (day.dayOfWeek == DayOfWeek.SATURDAY || day.dayOfWeek == DayOfWeek.SUNDAY) return@forEach // don't add weekends

                        val startOfLate = day
                            .atStartOfDayIn(TimeZone.currentSystemDefault())
                            .plus(Duration.parse("7h"))

                        val dayFound = attendances.find {
                            it.timestamp.toLocalDateTime(TimeZone.currentSystemDefault()).date == day
                        }

                        val presence = if (dayFound == null || !dayFound.isPresent) {
                            Presence.ABSENT
                        } else if (dayFound.timestamp > startOfLate) {
                            Presence.LATE
                        } else {
                            Presence.PRESENT
                        }

                        _calendarPresences.value = _calendarPresences.value.plus(
                            day to presence
                        )
                    }

                    onChangeFilter(_selectedFilter.value)
                    _isAttendancesLoaded.value = true
                }
                is GetAttendancesUseCase.Output.Failure -> {
                    _isAttendancesLoaded.value = false
                    Log.e("HistoryViewModel", "Failed to load attendances: ${result::class}")
                }
            }
        }
    }

    fun onChangeMonth(newMonth: YearMonth) {
        _selectedMonth.value = newMonth
        attendances = emptyList()
        onLoadAttendances()
    }

    fun onChangeAttendanceView(newAttendanceView: String) {
        _selectedAttendanceView.value = newAttendanceView
    }

    fun onChangeFilter(newFilter: String) {
        _selectedFilter.value = newFilter

        _filteredUiAttendances.value = attendances.map { attendance ->
            val startOfLate = attendance.timestamp
                .toLocalDateTime(TimeZone.currentSystemDefault()).date
                .atStartOfDayIn(TimeZone.currentSystemDefault())
                .plus(Duration.parse("7h"))

            val presence = when (attendance.isPresent) {
                true -> if (attendance.timestamp > startOfLate) Presence.LATE else Presence.PRESENT
                false -> Presence.ABSENT
            }

            val localTimestamp = attendance.timestamp.toLocalDateTime(TimeZone.currentSystemDefault())

            AttendanceDayUiModel(
                displayDate = localTimestamp.date.format(dateFormat),
                presence = presence,
                displayTimestamp = localTimestamp.format(timestampFormat),
                date = localTimestamp.date
            )
        }.filter { attendance ->
            when (newFilter) {
                context.getString(R.string.history_filter_all) -> true
                context.getString(R.string.history_filter_on_time) -> attendance.presence == Presence.PRESENT
                context.getString(R.string.history_filter_absent) -> attendance.presence == Presence.ABSENT
                context.getString(R.string.history_filter_late) -> attendance.presence == Presence.LATE
                else -> false
            }
        }
    }
}