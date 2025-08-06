package com.unovil.tardyscan.presentation.feature.scan

import android.content.Context
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.decodeToImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.unovil.tardyscan.domain.model.Student
import io.github.jan.supabase.storage.DownloadStatus
import kotlinx.coroutines.flow.filterNotNull

@Composable
fun SuccessfulScanCard(
    viewModel: ScanViewModel? = hiltViewModel(),
    isSubmittingEnabled: State<Boolean> = viewModel!!.isSubmittingEnabled.collectAsState(),
    scannedStudent: State<Student> = viewModel!!.scannedStudent.filterNotNull().collectAsState(
        Student(100_000_000_000, "", "", "", 0, "", "", null)
    ),
    onNavigate: () -> Unit = { },
    onSubmit: (Context) -> Unit = { },
    onReset: () -> Unit = {
        viewModel!!.onReset()
        onNavigate()
    },
) {
    val context = LocalContext.current
    val avatarState = scannedStudent.value.avatar?.collectAsState(
        DownloadStatus.Progress(0, 0)
    )

    val infiniteTransition = rememberInfiniteTransition("pulse")
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.6f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseAlpha"
    )

    val imageModifier = Modifier
        .fillMaxWidth()
        .height(240.dp)
        .background(Color.LightGray)
        .then(other =
            if (avatarState?.value !is DownloadStatus.ByteData) Modifier.alpha(pulseAlpha)
            else Modifier
        )

    val imagePainter = if (avatarState?.value is DownloadStatus.ByteData) {
        val byteData = (avatarState.value as DownloadStatus.ByteData).data
        val decodedBitmap = byteData.decodeToImageBitmap()
        BitmapPainter(decodedBitmap)
    } else {
        rememberVectorPainter(Icons.Default.Person)
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        ElevatedCard(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .statusBarsPadding()
        ) {
            Image(
                modifier = imageModifier,
                painter = imagePainter,
                contentScale = ContentScale.Crop,
                contentDescription = "Student picture"
            )

            Column(
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    modifier = Modifier.padding(bottom = 12.dp),
                    text = "Student scanned!",
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.headlineMedium
                )

                Spacer(modifier = Modifier.height(8.dp))

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    scannedStudent.value.let {
                        InfoRow("Name:", "${it.lastName}, ${it.firstName} ${it.middleName}")
                        InfoRow("Section:", "${it.level} - ${it.section}")
                        InfoRow("ID #:", "${it.id}")
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp, 8.dp, 0.dp, 0.dp),
                    colors = ButtonColors(
                        Color(0xFF246227),
                        Color.White,
                        Color.Gray,
                        Color.Black
                    ),
                    enabled = isSubmittingEnabled.value,
                    onClick = { onSubmit(context) }
                ) {
                    Text("Accept attendance")
                }
                Button(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(0.dp, 0.dp, 8.dp, 8.dp),
                    colors = ButtonColors(
                        Color(0xFF801717),
                        Color.White,
                        Color.Gray,
                        Color.Black
                    ),
                    enabled = isSubmittingEnabled.value,
                    onClick = onReset
                ) {
                    Text("Reject attendance")
                }
            }
        }
    }
}

@PreviewLightDark()
@Composable
private fun Preview() {
    val student = remember { mutableStateOf(
        Student(
            123456789123,
            "Last Name",
            "First Name",
            "Middle Name",
            10,
            "Sample Section",
            "Sample School",
            null
        )
    ) }

    val isSubmittingEnabled = remember { mutableStateOf(true) }
    SuccessfulScanCard(
        viewModel = null,
        scannedStudent = student,
        isSubmittingEnabled = isSubmittingEnabled,
        onNavigate = {},
        onReset = {},
        onSubmit = {}
    )
}

@Composable
private fun InfoRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .padding(4.dp)
            .height(IntrinsicSize.Min),
        horizontalArrangement = Arrangement.Start
    ) {
        Text(
            text = label,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .width(80.dp)
                .align(Alignment.Top)
        )
        Text(
            text = value,
            modifier = Modifier.weight(1f),
            overflow = TextOverflow.Visible
        )
    }
}