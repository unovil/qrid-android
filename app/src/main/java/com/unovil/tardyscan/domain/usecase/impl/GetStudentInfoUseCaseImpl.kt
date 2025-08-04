package com.unovil.tardyscan.domain.usecase.impl

import android.util.Log
import androidx.core.text.isDigitsOnly
import com.macasaet.fernet.Key
import com.macasaet.fernet.StringValidator
import com.macasaet.fernet.Token
import com.macasaet.fernet.TokenValidationException
import com.unovil.tardyscan.data.repository.AttendanceRepository
import com.unovil.tardyscan.domain.model.Student
import com.unovil.tardyscan.domain.usecase.GetStudentInfoUseCase
import io.github.jan.supabase.exceptions.HttpRequestException
import io.github.jan.supabase.postgrest.exception.PostgrestRestException
import io.github.jan.supabase.storage.DownloadStatus
import io.ktor.client.plugins.HttpRequestTimeoutException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.withContext
import java.time.temporal.ChronoUnit
import java.time.temporal.TemporalAmount
import javax.inject.Inject

class GetStudentInfoUseCaseImpl @Inject constructor(
    private val attendanceRepository: AttendanceRepository
) : GetStudentInfoUseCase {
    override suspend fun execute(input: GetStudentInfoUseCase.Input): GetStudentInfoUseCase.Output = withContext(Dispatchers.IO) {
        val qrFormat = "^(.*==)(.*)$".toRegex()
        val qrMatch = qrFormat.find(input.qrCode)
        if (qrMatch == null || qrMatch.groups.size < 3 || qrMatch.groups[2]?.value?.toIntOrNull() == null) {
            Log.e("GetStudentInfoUseCaseImpl", "Invalid QR Code format: ${input.qrCode}")
            return@withContext GetStudentInfoUseCase.Output.Failure.InvalidCode
        }

        val id: Long
        val lastName: String
        val firstName: String
        val middleName: String?
        val level: Int
        val section: String
        val school: String
        val avatarLink: String?

        try {
            val decryptionKey = attendanceRepository.getDecryptionKey(qrMatch.groups[2]!!.value.toInt())
            if (decryptionKey == null) {
                Log.e("GetStudentInfoUseCaseImpl", "Decryption key not found for school code: ${qrMatch.groups[1]!!.value}")
                return@withContext GetStudentInfoUseCase.Output.Failure.InvalidCode
            }

            val fernetKey = Key(decryptionKey)
            Log.d("GetStudentInfoUseCaseImpl", "Decrypting QR Code: ${qrMatch.groups[1]!!.value}")
            val decryptedString = Token.fromString(qrMatch.groups[1]!!.value)
                .validateAndDecrypt(fernetKey, object : StringValidator {
                    override fun getTimeToLive(): TemporalAmount? = ChronoUnit.YEARS.duration
                })

            if (decryptedString.length != 12 || !decryptedString.isDigitsOnly()) {
                Log.e("GetStudentInfoUseCaseImpl", "Invalid decrypted string: $decryptedString")
                return@withContext GetStudentInfoUseCase.Output.Failure.InvalidDecryption
            }

            val result = attendanceRepository.getStudentInfo(decryptedString.toLong())

            if (result == null) {
                Log.e("GetStudentInfoUseCaseImpl", "Student not found for ID: $decryptedString")
                return@withContext GetStudentInfoUseCase.Output.Failure.NotFound
            }

            id = decryptedString.toLong()
            lastName = result.lastName
            firstName = result.firstName
            middleName = result.middleName
            level = result.section.level
            section = result.section.section
            school = result.section.school.name
            avatarLink = result.avatarLink

        } catch(e: Exception) {
            Log.e("GetStudentInfoUseCaseImpl", "Error getting student info: ${e.message}")
            when (e) {
                is TokenValidationException, is IllegalArgumentException ->
                    return@withContext GetStudentInfoUseCase.Output.Failure.InvalidDecryption
                is HttpRequestException ->
                    return@withContext GetStudentInfoUseCase.Output.Failure.HttpRequestError
                is HttpRequestTimeoutException ->
                    return@withContext GetStudentInfoUseCase.Output.Failure.HttpRequestTimeout
                is PostgrestRestException ->
                    return@withContext GetStudentInfoUseCase.Output.Failure.PostgrestError(
                        e.message ?: "Unknown Postgrest error."
                    )
                else ->
                    return@withContext GetStudentInfoUseCase.Output.Failure.UnknownError(
                        e.message ?: "Unknown error."
                    )
            }
        }

        val avatar = try {
            if (avatarLink == null) null else attendanceRepository.getAvatarFlow(avatarLink).catch { e ->
                Log.e("GetStudentInfoUseCaseImpl", "Error getting avatar: ${e.message}")
                emit(DownloadStatus.Progress(-1,-1))
            }
        } catch(e: Exception) {
            Log.d("GetStudentInfoUseCaseImpl", "Error getting avatar: ${e.message}")
            null
        }

        val student = Student(id, lastName, firstName, middleName, level, section, school, avatar)

        return@withContext GetStudentInfoUseCase.Output.Success(student)
    }
}