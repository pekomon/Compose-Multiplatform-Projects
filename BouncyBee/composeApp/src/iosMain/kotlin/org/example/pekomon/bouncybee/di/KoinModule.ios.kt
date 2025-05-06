package org.example.pekomon.bouncybee.di

import org.example.pekomon.bouncybee.domain.AudioPlayer
import org.koin.core.module.Module
import org.koin.dsl.module

actual val targetModule: Module = module {
    single<AudioPlayer> { AudioPlayer() }
}

