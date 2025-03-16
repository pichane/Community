package com.example.myapp.presentation.screen.onboarding

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.myapp.R
import com.example.myapp.presentation.theme.dimensions

/**
 * only for the Demo... no point to review this file
 */
@Composable
fun OnBoardingScreen(onGetStartedClick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(MaterialTheme.dimensions.iconSmall),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.ic_app_icon),
            contentDescription = stringResource(R.string.app_logo),
            modifier = Modifier.size(200.dp)
        )
        
        Spacer(modifier = Modifier.height(MaterialTheme.dimensions.iconLarge))
        
        Text(
            text = stringResource(R.string.welcome_to_my_app),
            style = MaterialTheme.typography.headlineMedium,
            textAlign = TextAlign.Center
        )
        
        Spacer(modifier = Modifier.height(MaterialTheme.dimensions.iconSmall))
        
        Text(
            text = stringResource(R.string.this_app_demonstrates_clean_architecture_with_jetpack_compose),
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center
        )
        
        Spacer(modifier = Modifier.height(MaterialTheme.dimensions.iconLarge))
        
        Button(
            onClick = onGetStartedClick,
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
        ) {
            Text(text = stringResource(R.string.get_started))
        }
    }
}