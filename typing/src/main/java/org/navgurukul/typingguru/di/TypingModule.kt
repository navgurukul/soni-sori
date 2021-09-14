package org.navgurukul.typingguru.di

import org.koin.android.ext.koin.androidApplication
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import org.navgurukul.typingguru.keyboard.KeyboardActivityArgs
import org.navgurukul.typingguru.keyboard.KeyboardViewModel
import org.navgurukul.typingguru.keyboard.dialog.KeyboardDialogViewModel
import org.navgurukul.typingguru.score.ScoreActivityArgs
import org.navgurukul.typingguru.score.ScoreViewModel
import org.navgurukul.typingguru.keyboard.dialog.KeyboardDialogArgs
import org.navgurukul.typingguru.utils.SystemUtils
import org.navgurukul.typingguru.utils.TypingGuruPreferenceManager
import org.navgurukul.typingguru.webview.WebViewActivityViewModel

val typingModule = module {
    single { SystemUtils(androidApplication()) }
    single { TypingGuruPreferenceManager(get()) }

    viewModel { (scoreActivityArgs: ScoreActivityArgs) -> ScoreViewModel(scoreActivityArgs, get()) }
    viewModel { WebViewActivityViewModel(get(), get()) }
    viewModel { (keyboardDialogArgs: KeyboardDialogArgs) -> KeyboardDialogViewModel (keyboardDialogArgs, get(), get(), get()) }
    viewModel { (keyboardActivityArgs: KeyboardActivityArgs) -> KeyboardViewModel (keyboardActivityArgs) }
}