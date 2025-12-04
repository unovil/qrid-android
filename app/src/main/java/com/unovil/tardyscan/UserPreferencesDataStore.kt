package com.unovil.tardyscan

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.dataStore
import com.unovil.tardyscan.data.datastore.SettingsSerializer
import com.unovil.tardyscan.proto.Settings

val Context.dataStore: DataStore<Settings> by dataStore(
    fileName = "settings.pb",
    serializer = SettingsSerializer
)