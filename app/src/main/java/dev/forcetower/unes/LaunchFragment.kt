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

import android.os.Bundle
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import dev.forcetower.core.base.BaseDaggerFragment
import dev.forcetower.core.base.BaseViewModelFactory
import javax.inject.Inject

class LaunchFragment : BaseDaggerFragment() {
    @Inject lateinit var factory: BaseViewModelFactory
    private val viewModel by viewModels<LaunchViewModel> { factory }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (savedInstanceState == null) {
            viewModel.isConnected().observe(this, Observer {
                val direction = if (it) {
                    LaunchFragmentDirections.dashboard()
                } else {
                    LaunchFragmentDirections.selectInstitution()
                }
                findNavController().navigate(direction)
            })
        }
    }
}