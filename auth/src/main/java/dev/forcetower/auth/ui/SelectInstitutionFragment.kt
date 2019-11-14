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

package dev.forcetower.auth.ui

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import dev.forcetower.auth.dagger.DaggerAuthComponent
import dev.forcetower.auth.databinding.FragmentSelectInstitutionBinding
import dev.forcetower.core.base.BaseFragment
import dev.forcetower.core.base.BaseViewModelFactory
import dev.forcetower.unes.coreComponent
import javax.inject.Inject

class SelectInstitutionFragment : BaseFragment() {
    @Inject lateinit var factory: BaseViewModelFactory
    private lateinit var binding: FragmentSelectInstitutionBinding
    private val viewModel: AuthViewModel by activityViewModels { factory }

    override fun onAttach(context: Context) {
        DaggerAuthComponent.builder().coreComponent(coreComponent()).build().inject(this)
        super.onAttach(context)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return FragmentSelectInstitutionBinding.inflate(inflater, container, false).also {
            binding = it
        }.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.selectedInstitution.observe(viewLifecycleOwner, Observer {
            binding.next.isEnabled = true
            binding.internalSelectInstitution.setText(it)
        })

        binding.apply {
            internalSelectInstitution.setOnClickListener {
                val direction = SelectInstitutionFragmentDirections.selectInstitution()
                findNavController().navigate(direction)
            }
            next.setOnClickListener {
                val direction = SelectInstitutionFragmentDirections.login()
                findNavController().navigate(direction)
            }
        }
    }
}