package com.unovil.tardyscan.core.domain

interface UseCase<InputT, OutputT> {
    suspend fun execute(input: InputT): OutputT
}