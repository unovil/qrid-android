package com.unovil.tardyscan.domain.usecase

interface AsyncUseCase<InputT, OutputT> {
    fun execute(input: InputT): OutputT
}