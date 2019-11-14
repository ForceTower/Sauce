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
import androidx.annotation.StringRes
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
import com.forcetower.sagres.SagresNavigator
import com.google.android.material.snackbar.Snackbar
import dev.forcetower.auth.R
import dev.forcetower.auth.databinding.FragmentLoginBinding
import dev.forcetower.core.base.BaseDaggerFragment
import dev.forcetower.core.base.BaseViewModelFactory
import timber.log.Timber
import javax.inject.Inject

class LoginFragment : BaseDaggerFragment() {
    @Inject
    lateinit var factory: BaseViewModelFactory
    private lateinit var binding: FragmentLoginBinding
    private val viewModel by activityViewModels<AuthViewModel> { factory }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return FragmentLoginBinding.inflate(inflater, container, false).also {
            binding = it
        }.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.selectedInstitution.observe(viewLifecycleOwner, Observer {
            binding.selectInstitution.text = getString(R.string.selected_institution, it)
        })
        viewModel.loginError.observe(viewLifecycleOwner, Observer { onLoginError(it) })
        binding.selectInstitution.setOnClickListener {
            dismissCurrentSnack()
            findNavController().popBackStack()
        }
        binding.next.setOnClickListener { onLogin() }
        binding.username.setOnFocusChangeListener { _, _ -> dismissCurrentSnack() }
        binding.password.setOnFocusChangeListener { _, _ -> dismissCurrentSnack() }
        Timber.d(binding.logoAuth.transitionName)
    }

    private fun onLogin() {
        dismissCurrentSnack()
        val username = binding.username.text.toString()
        val password = binding.password.text.toString()

        if (username.length < 4) {
            binding.username.error = getString(R.string.username_too_short)
            binding.username.requestFocus()
            return
        }
        if (password.length < 4) {
            binding.password.error = getString(R.string.password_too_short)
            binding.password.requestFocus()
            return
        }

        val extras = FragmentNavigatorExtras(binding.logoAuth to binding.logoAuth.transitionName)
        val directions = LoginFragmentDirections.loggingIn(username, password, SagresNavigator.instance.getSelectedInstitution())
        findNavController().navigate(directions, extras)
    }

    private fun onLoginError(@StringRes resource: Int) {
        showSnack(getString(resource), Snackbar.LENGTH_LONG)
    }
}