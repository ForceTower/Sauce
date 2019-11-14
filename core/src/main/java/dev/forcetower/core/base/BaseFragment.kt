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

package dev.forcetower.core.base

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.core.view.updatePaddingRelative
import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.Snackbar
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasAndroidInjector
import dagger.android.support.AndroidSupportInjection
import dev.forcetower.core.extensions.doOnApplyWindowInsets
import timber.log.Timber
import javax.inject.Inject

abstract class BaseFragment : Fragment() {
    private var currentSnackbar: Snackbar? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (shouldApplyBottomInsets()) {
            view.doOnApplyWindowInsets { v, insets, padding ->
                v.updatePaddingRelative(bottom = padding.bottom + insets.systemWindowInsetBottom)
            }
        }
    }

    open fun shouldApplyBottomInsets() = true

    fun dismissCurrentSnack() = currentSnackbar?.dismiss()

    fun showSnack(string: String, duration: Int = Snackbar.LENGTH_SHORT) {
        val snack = getSnack(string, duration)
        currentSnackbar = snack
        snack?.show()
    }

    fun getSnack(string: String, duration: Int = Snackbar.LENGTH_SHORT): Snackbar? {
        val activity = activity
        return if (activity is BaseActivity) {
            activity.getSnackInstance(string, duration)
        } else {
            Timber.d("Not part of UActivity")
            null
        }
    }
}

abstract class BaseDaggerFragment : BaseFragment(), HasAndroidInjector {
    @Inject
    lateinit var androidInjector: DispatchingAndroidInjector<Any>

    override fun onAttach(context: Context) {
        AndroidSupportInjection.inject(this)
        super.onAttach(context)
    }

    override fun androidInjector() = androidInjector
}