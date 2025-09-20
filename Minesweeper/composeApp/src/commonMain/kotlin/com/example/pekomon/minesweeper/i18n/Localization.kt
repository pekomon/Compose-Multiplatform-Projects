package com.example.pekomon.minesweeper.i18n

import androidx.compose.runtime.Composable
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource

@Composable
fun t(res: StringResource, vararg args: Any): String = stringResource(res, *args)
