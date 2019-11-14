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

package dev.forcetower.dashboard.ui

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.updatePaddingRelative
import dev.forcetower.unes.base.BaseFragment
import dev.forcetower.core.extensions.doOnApplyWindowInsets
import dev.forcetower.unes.coreComponent
import dev.forcetower.dashboard.dagger.DaggerDashboardComponent
import dev.forcetower.dashboard.databinding.FragmentDashboardBinding

class DashboardFragment : BaseFragment() {
    private lateinit var binding: FragmentDashboardBinding
    private lateinit var adapter: ElementAdapter

    override fun onAttach(context: Context) {
        DaggerDashboardComponent.builder().coreComponent(coreComponent()).build().inject(this)
        super.onAttach(context)
    }

    override fun shouldApplyBottomInsets() = false

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        adapter = ElementAdapter()
        return FragmentDashboardBinding.inflate(inflater, container, false).also {
            binding = it
        }.apply {
            binding.recyclerElements.adapter = adapter
            binding.recyclerElements.itemAnimator?.apply {
                changeDuration = 0
            }
        }.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.recyclerElements.doOnApplyWindowInsets { v, insets, padding ->
            v.updatePaddingRelative(bottom = padding.bottom + insets.systemWindowInsetBottom)
        }
    }
}