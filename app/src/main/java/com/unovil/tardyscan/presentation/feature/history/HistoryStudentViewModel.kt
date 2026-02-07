package com.unovil.tardyscan.presentation.feature.history

import android.content.Context
import androidx.lifecycle.ViewModel
import com.unovil.tardyscan.domain.usecase.GetAttendancesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

@HiltViewModel
class HistoryStudentViewModel @Inject constructor(
    private val getAttendancesUseCase: GetAttendancesUseCase,
    @ApplicationContext context: Context
) : ViewModel() {

}