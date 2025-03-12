package com.unovil.tardyscan.dialogs

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

@Composable
fun CameraRequiredDialog(onDismiss: () -> Unit, onConfirm: () -> Unit) {
    AlertDialog (
        onDismissRequest = onDismiss,
        title = { Text("Camera Required") },
        text = { Text("This app needs camera access to scan QR codes. Please grant the permission.") },
        confirmButton = {
            Button(onClick = onConfirm) {
                Text("Grant")
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text("Dismiss")
            }
        }

    )
}