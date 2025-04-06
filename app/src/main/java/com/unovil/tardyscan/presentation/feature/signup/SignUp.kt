package com.unovil.tardyscan.presentation.feature.signup

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController

@Composable
fun SignUp(
    viewModel: SignUpViewModel = hiltViewModel(),
    navController: NavController = rememberNavController()
) {
    Text("Sign up here!")
}