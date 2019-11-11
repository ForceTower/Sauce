/*
 * This file is part of the UNES Open Source Project.
 * UNES is licensed under the GNU GPLv3.
 *
 * Copyright (c) 2019. João Paulo Sena <joaopaulo761@gmail.com>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package dev.forcetower.unes

import android.content.Context
import android.content.SharedPreferences
import com.forcetower.sagres.SagresNavigator
import dagger.android.AndroidInjector
import dagger.android.support.DaggerApplication
import dev.forcetower.core.Constants
import dev.forcetower.core.dagger.CoreComponent
import dev.forcetower.core.dagger.DaggerCoreComponent
import dev.forcetower.core.impl.juice.AndroidBase64Encoder
import dev.forcetower.core.impl.juice.PrefsCookiePersistor
import dev.forcetower.unes.dagger.DaggerAppComponent
import timber.log.Timber
import javax.inject.Inject

class UNESApp : DaggerApplication() {
    @Inject lateinit var preferences: SharedPreferences

    private val coreComponent: CoreComponent by lazy { DaggerCoreComponent.builder().context(this).build() }

    override fun onCreate() {
        super.onCreate()
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
    }

    override fun applicationInjector(): AndroidInjector<out DaggerApplication> {
        return DaggerAppComponent.builder()
            .coreComponent(coreComponent)
            .application(this)
            .build()
    }

    @Inject
    fun configureSagresNavigator() {
        val selected = preferences.getString(Constants.SELECTED_INSTITUTION_KEY, "UEFS") ?: "UEFS"
        SagresNavigator.initialize(PrefsCookiePersistor(this), selected, AndroidBase64Encoder())
    }

    companion object {
        @JvmStatic
        fun coreComponent(context: Context) = (context.applicationContext as UNESApp).coreComponent
    }
}