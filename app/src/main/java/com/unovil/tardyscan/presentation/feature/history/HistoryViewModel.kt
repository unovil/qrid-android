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
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn
import kotlinx.datetime.toLocalDateTime
import javax.inject.Inject
import kotlin.time.Duration

@HiltViewModel
class HistoryViewModel @Inject constructor(
    // private val getStudentInfoUseCase: GetStudentInfoUseCase,
    private val getAttendancesUseCase: GetAttendancesUseCase
) : ViewModel() {

    val attendanceFilterOptions = listOf("On time", "Absent", "Late", "All")

    private var attendances = listOf<Attendance>()

    private val _filteredAttendances = MutableStateFlow<List<Attendance>>(emptyList())
    val filteredAttendances = _filteredAttendances.asStateFlow()

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
                    val datedAttendances = result.attendanceList.sortedWith(
                        compareBy<Attendance> { it.name.trim() }
                    )
                        /*if (_selectedFilter.value == "All") {
                            sortedWith( compareBy<Attendance> { it.name.trim() } )
                        } else {
                            sortedWith(compareByDescending<Attendance> { it.isPresent }
                                .thenBy { it.name.trim() }
                            )
                        }*/
                    attendances = datedAttendances

                    onChangeFilter(_selectedFilter.value)
                }
                is GetAttendancesUseCase.Output.Failure -> {
                    Log.e("HistoryViewModel", "Failed to load attendances: ${result.e.message}")
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
        if (newFilter == "All") {
            _filteredAttendances.value = attendances
            return
        }

        val startOfLate = _selectedTimestamp.value
            .toLocalDateTime(TimeZone.currentSystemDefault()).date
            .atStartOfDayIn(TimeZone.currentSystemDefault())
            .plus(Duration.parse("7h"))

        Log.d("HistoryViewModel", "Start of late in onChangeFilter: $startOfLate")

        if (newFilter !in attendanceFilterOptions) return
        _filteredAttendances.value = attendances.filter {
            if (it.studentId == 535317030410L)
                Log.d("HistoryViewModel", "Test called in onChangeFilter, ${it.timestamp}")

            return@filter when (newFilter) {
                "On time" -> (it.timestamp <= startOfLate && it.isPresent)
                "Absent" -> (!it.isPresent)
                "Late" -> (it.timestamp > startOfLate && it.isPresent)
                else -> false
            }
        }
    }
}