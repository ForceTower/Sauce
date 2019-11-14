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
import androidx.navigation.fragment.navArgs
import dev.forcetower.auth.R
import dev.forcetower.auth.dagger.DaggerAuthComponent
import dev.forcetower.auth.databinding.FragmentConnectingBinding
import dev.forcetower.core.base.BaseDaggerFragment
import dev.forcetower.core.base.BaseFragment
import dev.forcetower.core.base.BaseViewModelFactory
import dev.forcetower.unes.coreComponent
import timber.log.Timber
import javax.inject.Inject

class ConnectingFragment : BaseFragment() {
    @Inject lateinit var factory: BaseViewModelFactory
    private lateinit var binding: FragmentConnectingBinding
    private val viewModel by activityViewModels<AuthViewModel> { factory }
    private val args by navArgs<ConnectingFragmentArgs>()

    override fun onAttach(context: Context) {
        DaggerAuthComponent.builder().coreComponent(coreComponent()).build().inject(this)
        super.onAttach(context)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return FragmentConnectingBinding.inflate(inflater, container, false).also {
            binding = it
        }.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (savedInstanceState == null) {
            Timber.d("Execute login command")
            viewModel.executeLogin(args.username, args.password, args.institution)
        }
        Timber.d(binding.logoAuth.transitionName)
        viewModel.loginStatus.observe(viewLifecycleOwner, Observer { onLoginResult(it) })
        viewModel.backSignal.observe(viewLifecycleOwner, Observer {
            Timber.d("Back signal received")
            findNavController().popBackStack()
        })
    }

    private fun onLoginResult(code: Int) {
        binding.progressHorizontal.visibility = View.GONE
        when (code) {
            200 -> onLoginSuccess()
            401 -> onInvalidCredentials()
            440 -> onSessionTimeout()
            else -> onUnknownError()
        }
    }

    private fun onUnknownError() {
        binding.connectingText.setText(R.string.login_error_main_display)
        viewModel.setLoginError(R.string.login_error_unknown_error)
    }

    private fun onSessionTimeout() {
        binding.connectingText.setText(R.string.login_error_main_display)
        viewModel.setLoginError(R.string.login_error_session_timeout)
    }

    private fun onInvalidCredentials() {
        binding.connectingText.setText(R.string.login_error_main_display)
        viewModel.setLoginError(R.string.login_error_invalid_credentials)
    }

    private fun onLoginSuccess() {
        binding.connectingText.setText(R.string.connection_successfull)
    }
}