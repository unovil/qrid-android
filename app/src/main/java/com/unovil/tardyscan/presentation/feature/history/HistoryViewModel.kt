package com.unovil.tardyscan.presentation.feature.history

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.unovil.tardyscan.domain.model.Attendance
import com.unovil.tardyscan.domain.usecase.GetAttendancesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn
import kotlinx.datetime.format
import kotlinx.datetime.format.MonthNames
import kotlinx.datetime.toLocalDateTime
import javax.inject.Inject
import kotlin.time.Duration

@HiltViewModel
class HistoryViewModel @Inject constructor(
    // private val getStudentInfoUseCase: GetStudentInfoUseCase,
    private val getAttendancesUseCase: GetAttendancesUseCase
) : ViewModel() {

    val attendanceFilterOptions = listOf("On time", "Absent", "Late", "All")
    private val timestampFormat = LocalDateTime.Format {
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

    private var attendances = listOf<Attendance>()
    private val _isAttendancesLoaded = MutableStateFlow(true)
    val isAttendancesLoaded = _isAttendancesLoaded.asStateFlow()

    private val _filteredUiAttendances = MutableStateFlow<List<AttendanceUiModel>>(emptyList())
    val filteredUiAttendances = _filteredUiAttendances.asStateFlow()

    private val _selectedTimestamp = MutableStateFlow(Clock.System.now())
    val selectedTimestamp = _selectedTimestamp.asStateFlow()
    private val _selectedFilter = MutableStateFlow(attendanceFilterOptions[0])
    val selectedFilter = _selectedFilter.asStateFlow()

    fun onLoadAttendances() {
        viewModelScope.launch {
            when (val result = getAttendancesUseCase.execute(GetAttendancesUseCase.Input(
                _selectedTimestamp.value.toLocalDateTime(
                    TimeZone.currentSystemDefault()
                ).date
            ))) {
                is GetAttendancesUseCase.Output.Success -> {
                    val datedAttendances = result.attendanceList.sortedWith(comparator =
                        compareBy<Attendance> { it.level }
                            .thenBy { it.section.lowercase() }
                            .thenBy { it.name.trim() }
                    )
                    attendances = datedAttendances

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

    fun onChangeDate(newDate: LocalDate) {
        _selectedTimestamp.value = newDate.atStartOfDayIn(TimeZone.currentSystemDefault())
        attendances = emptyList()
        onLoadAttendances()
    }

    fun onChangeFilter(newFilter: String) {
        _selectedFilter.value = newFilter

        val startOfLate = _selectedTimestamp.value
            .toLocalDateTime(TimeZone.currentSystemDefault()).date
            .atStartOfDayIn(TimeZone.currentSystemDefault())
            .plus(Duration.parse("7h"))

        Log.d("HistoryViewModel", "Start of late in onChangeFilter: $startOfLate")

        _filteredUiAttendances.value = attendances.map { attendance ->
            val presence = when (attendance.isPresent) {
                true -> if (attendance.timestamp > startOfLate) Presence.LATE else Presence.PRESENT
                false -> Presence.ABSENT
            }

            AttendanceUiModel(
                id = attendance.studentId,
                name = attendance.name,
                level = attendance.level,
                section = attendance.section,
                presence = presence,
                displayTimestamp = attendance.timestamp
                    .toLocalDateTime(TimeZone.currentSystemDefault())
                    .format(timestampFormat).toString()
            )
        }.filter { attendance ->
            when (newFilter) {
                "On time" -> attendance.presence == Presence.PRESENT
                "Absent" -> attendance.presence == Presence.ABSENT
                "Late" -> attendance.presence == Presence.LATE
                "All" -> true
                else -> false
            }
        }

    }
}