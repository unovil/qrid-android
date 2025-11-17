package com.unovil.tardyscan.core.domain

import com.unovil.tardyscan.core.model.AvatarState
import kotlinx.coroutines.flow.Flow

interface GetStudentAvatarUseCase : UseCase<GetStudentAvatarUseCase.Input, GetStudentAvatarUseCase.Output> {
    class Input (val avatarUrl: String)

    sealed class Output {
        class Success(val avatarFlow: Flow<AvatarState>) : Output()
        class Failure(val error: String) : Output()
    }
}