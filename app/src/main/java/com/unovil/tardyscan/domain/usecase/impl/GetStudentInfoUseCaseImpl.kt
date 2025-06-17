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
import kotlinx.coroutines.Dispatchers
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

        val decryptionKey = attendanceRepository.getDecryptionKey(qrMatch.groups[2]!!.value.toInt())
        if (decryptionKey == null) {
            Log.e("GetStudentInfoUseCaseImpl", "Decryption key not found for school code: ${qrMatch.groups[1]!!.value}")
            return@withContext GetStudentInfoUseCase.Output.Failure.InvalidCode
        }

        val decryptedString : String
        try {
            val fernetKey = Key(decryptionKey)
            Log.d("GetStudentInfoUseCaseImpl", "Decrypting QR Code: ${qrMatch.groups[1]!!.value}")
            decryptedString = Token.fromString(qrMatch.groups[1]!!.value)
                .validateAndDecrypt(fernetKey, object : StringValidator {
                    override fun getTimeToLive(): TemporalAmount? = ChronoUnit.YEARS.duration
                })

            if (decryptedString.length != 12 || !decryptedString.isDigitsOnly()) {
                Log.e("GetStudentInfoUseCaseImpl", "Invalid decrypted string: $decryptedString")
                return@withContext GetStudentInfoUseCase.Output.Failure.InvalidDecryption
            }
        } catch (e: Exception) {
            if (e is TokenValidationException || e is IllegalArgumentException) {
                e.printStackTrace()
                return@withContext GetStudentInfoUseCase.Output.Failure.InvalidDecryption
            }

            throw e
        }

        val result = attendanceRepository.getStudentInfo(decryptedString.toLong())

        if (result == null) {
            Log.e("GetStudentInfoUseCaseImpl", "Student not found for ID: $decryptedString")
            return@withContext GetStudentInfoUseCase.Output.Failure.NotFound
        }

        val student = Student(
            decryptedString.toLong(),
            result.lastName,
            result.firstName,
            result.middleName,
            result.section.level,
            result.section.section,
            result.section.school.name
        )

        return@withContext GetStudentInfoUseCase.Output.Success(student)
    }
}