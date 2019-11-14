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

import androidx.annotation.MainThread
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.forcetower.auth.core.AuthRepository
import dev.forcetower.core.data.SingleLiveEvent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject

class AuthViewModel @Inject constructor(
    private val repository: AuthRepository
) : ViewModel() {
    private val _selectedInstitution = MutableLiveData<String>()
    val selectedInstitution: LiveData<String>
        get() = _selectedInstitution

    private val _loginStatus = SingleLiveEvent<Int>()
    val loginStatus: LiveData<Int>
        get() = _loginStatus

    private val _backSignal = SingleLiveEvent<Int>()
    val backSignal: LiveData<Int>
        get() = _backSignal

    private val _loginError = SingleLiveEvent<Int>()
    val loginError: LiveData<Int>
        get() = _loginError

    @MainThread
    fun setSelectedInstitution(institution: String)  {
        _selectedInstitution.value = institution
    }

    fun executeLogin(username: String, password: String, institution: String) {
        viewModelScope.launch {
            val result = repository.executeLogin(username, password, institution)
            _loginStatus.value = result
            delay(1000)
            _backSignal.value = if (result == 200) 0 else 1
        }
    }

    @MainThread
    fun setLoginError(value: Int) {
        _loginError.value = value
    }
}