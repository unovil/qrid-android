package com.unovil.tardyscan.core.domain.impl

import com.unovil.tardyscan.core.data.repository.AttendanceRepository
import com.unovil.tardyscan.core.domain.GetStudentAvatarUseCase
import com.unovil.tardyscan.core.model.AvatarState
import io.github.jan.supabase.storage.DownloadStatus
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject

class GetStudentAvatarUseCaseImpl @Inject constructor(
    private val attendanceRepository: AttendanceRepository
) : GetStudentAvatarUseCase {
    override suspend fun execute(input: GetStudentAvatarUseCase.Input): GetStudentAvatarUseCase.Output = withContext(Dispatchers.IO) {
        val avatarFlow = attendanceRepository.getAvatarFlow(input.avatarUrl)
            .map { status ->
                when (status) {
                    is DownloadStatus.Progress -> AvatarState.Loading
                    is DownloadStatus.ByteData -> AvatarState.Downloaded(status.data)
                    else -> AvatarState.NotDownloaded("Error downloading avatar")
                }
            }
            .catch { e -> AvatarState.NotDownloaded(e.message ?: "Error downloading avatar (caught)")}

        GetStudentAvatarUseCase.Output.Success(avatarFlow)
    }
}