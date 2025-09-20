package com.example.pekomon.minesweeper.i18n

import androidx.compose.runtime.Composable
import com.example.pekomon.minesweeper.game.Difficulty
import com.example.pekomon.minesweeper.generated.resources.MR
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.getString
import org.jetbrains.compose.resources.stringResource

@Composable
fun localizedString(resource: StringResource, vararg formatArgs: Any?): String =
    stringResource(resource, *formatArgs)

suspend fun localizedString(resource: StringResource, vararg formatArgs: Any?): String =
    getString(resource, *formatArgs)

@Composable
fun Difficulty.localizedName(): String =
    when (this) {
        Difficulty.EASY -> localizedString(MR.strings.difficulty_easy)
        Difficulty.MEDIUM -> localizedString(MR.strings.difficulty_medium)
        Difficulty.HARD -> localizedString(MR.strings.difficulty_hard)
    }
