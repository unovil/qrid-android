package com.unovil.tardyscan.di

import android.content.Context
import com.unovil.tardyscan.dataStore
import com.unovil.tardyscan.proto.ThemeConfig
import com.unovil.tardyscan.proto.copy
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SettingsRepository @Inject constructor(
    @ApplicationContext private val context: Context
) {
    val themeFlow: Flow<ThemeConfig> = context.dataStore.data.map { settings ->
        settings.theme
    }

    val nameFlow: Flow<String> = context.dataStore.data.map { settings ->
        settings.name
    }

    suspend fun setTheme(newTheme: ThemeConfig) {
        context.dataStore.updateData { settings ->
            settings.copy { theme = newTheme }
        }
    }

    suspend fun setName(newName: String) {
        context.dataStore.updateData { settings ->
            settings.copy { name = newName }
        }
    }

}