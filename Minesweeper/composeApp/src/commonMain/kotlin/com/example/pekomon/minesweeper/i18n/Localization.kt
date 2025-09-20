package com.example.pekomon.minesweeper.i18n

import androidx.compose.runtime.Composable
import androidx.compose.runtime.staticCompositionLocalOf
import com.example.pekomon.minesweeper.game.Difficulty
import minesweeper.composeapp.generated.resources.MR
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.getString
import org.jetbrains.compose.resources.stringResource

/** Represents a supported locale using a BCP-47 language tag. */
data class AppLocale(val languageTag: String)

/** Composition local holding the current app locale. */
val LocalAppLocale = staticCompositionLocalOf { AppLocales.Default }

/** Supported locales and helper utilities. */
object AppLocales {
    val English = AppLocale("en")
    val Finnish = AppLocale("fi")

    val supported: List<AppLocale> = listOf(English, Finnish)

    val Default: AppLocale = English
}

@Composable
fun localizedString(resource: StringResource, vararg formatArgs: Any?): String {
    val locale = LocalAppLocale.current
    return stringResource(resource, *formatArgs, locale = locale.languageTag)
}

suspend fun localizedString(
    locale: AppLocale,
    resource: StringResource,
    vararg formatArgs: Any?,
): String = getString(resource, locale = locale.languageTag, *formatArgs)

@Composable
fun AppLocale.displayName(): String {
    val resource = when (languageTag) {
        AppLocales.Finnish.languageTag -> MR.strings.language_finnish
        else -> MR.strings.language_english
    }
    return localizedString(resource)
}

@Composable
fun Difficulty.localizedName(): String =
    when (this) {
        Difficulty.EASY -> localizedString(MR.strings.difficulty_easy)
        Difficulty.MEDIUM -> localizedString(MR.strings.difficulty_medium)
        Difficulty.HARD -> localizedString(MR.strings.difficulty_hard)
    }
