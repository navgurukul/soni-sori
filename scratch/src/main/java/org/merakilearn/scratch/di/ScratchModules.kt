package org.merakilearn.scratch.di

import org.koin.dsl.module
import org.merakilearn.core.navigator.ScratchModuleNavigator
import org.merakilearn.scratch.navigation.ScratchModuleNavigatorImpl


val scratchModules = module {
    single<ScratchModuleNavigator> { ScratchModuleNavigatorImpl() }
}