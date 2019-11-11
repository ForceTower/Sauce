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

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.preference.PreferenceManager
import com.forcetower.sagres.SagresNavigator
import dev.forcetower.auth.databinding.DialogSelectInstitutionBinding
import dev.forcetower.core.Constants
import dev.forcetower.core.base.BaseDialogDaggerFragment
import dev.forcetower.core.base.BaseDialogFragment
import dev.forcetower.core.base.BaseViewModelFactory
import timber.log.Timber
import javax.inject.Inject

class SelectInstitutionDialog : BaseDialogDaggerFragment() {
    @Inject lateinit var factory: BaseViewModelFactory
    private lateinit var binding: DialogSelectInstitutionBinding
    private val viewModel: AuthViewModel by activityViewModels { factory }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return DialogSelectInstitutionBinding.inflate(inflater, container, false).also {
            binding = it
        }.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            btnOk.setOnClickListener {
                saveSelectedInstitution()
                dismiss()
            }
            btnCancel.setOnClickListener {
                dismiss()
            }
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        populateInstitutions()
    }

    private fun populateInstitutions() {
        val institutions = SagresNavigator.getSupportedInstitutions()
        binding.apply {
            pickerInstitution.minValue = 1
            pickerInstitution.maxValue = institutions.size
            pickerInstitution.displayedValues = institutions
            val currentIndex = institutions.indexOf(viewModel.selectedInstitution.value ?: "UEFS") + 1
            pickerInstitution.value = if (currentIndex == 0) 1 else currentIndex
        }
    }

    private fun saveSelectedInstitution() {
        val institutions = SagresNavigator.getSupportedInstitutions()
        val selected = institutions[binding.pickerInstitution.value - 1]
        Timber.d("Selected $selected")
        viewModel.setSelectedInstitution(selected)
        val preferences = PreferenceManager.getDefaultSharedPreferences(context)
        preferences.edit().putString(Constants.SELECTED_INSTITUTION_KEY, selected).apply()
        SagresNavigator.instance.setSelectedInstitution(selected)
    }
}