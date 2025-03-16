package com.example.myapp.presentation.screen.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.myapp.R
import com.example.myapp.presentation.common.debouncedClickable
import com.example.myapp.presentation.theme.dimensions
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel = koinViewModel(),
    onTakePhotoClick: () -> Unit,
    onPhotoClick: (Int) -> Unit,
    onSocialClick: () -> Unit,
    onMemoryClick: (String, String) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackBarHostState = remember { SnackbarHostState() }

    // Error handling
    LaunchedEffect(uiState.error) {
        uiState.error?.let { error ->
            snackBarHostState.showSnackbar(error)
            viewModel.onEvent(HomeEvent.ErrorShown)
        }
    }

    Scaffold(
        topBar = {
            HomeTopBar(onSocialClick)
        },
        snackbarHost = {
            SnackbarHost(snackBarHostState)
        }
    ) { paddingValues ->
        HomeContent(
            uiState = uiState,
            onTakePhotoClick = onTakePhotoClick,
            onPhotoClick = onPhotoClick,
            onMemoryClick = onMemoryClick,
            paddingValues = paddingValues
        )
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun HomeTopBar(onSocialClick: () -> Unit) {
    TopAppBar(
        title = {
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_app_icon),
                    contentDescription = stringResource(R.string.app_logo),
                    modifier = Modifier
                        .size(MaterialTheme.dimensions.iconExtraLarge)
                        .padding(MaterialTheme.dimensions.spaceExtraSmall)
                )
            }
        },
        navigationIcon = {
            Icon(
                imageVector = Icons.Default.Menu,
                contentDescription = stringResource(R.string.social),
                modifier = Modifier.debouncedClickable { onSocialClick() }
            )
        }
    )
}
