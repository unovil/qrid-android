package com.unovil.tardyscan.core.model

sealed interface AvatarState {
    data object Loading : AvatarState
    data class Downloaded(val bytes: ByteArray) : AvatarState {
        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false

            other as Downloaded

            return bytes.contentEquals(other.bytes)
        }

        override fun hashCode(): Int {
            return bytes.contentHashCode()
        }
    }

    data class NotDownloaded(val reason: String) : AvatarState
}