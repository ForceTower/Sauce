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

package dev.forcetower.sync

import com.forcetower.sagres.SagresNavigator
import com.forcetower.sagres.operation.Status
import com.forcetower.sagres.operation.login.LoginCallback
import dev.forcetower.database.base.UNESDatabase
import dev.forcetower.model.base.Credential
import kotlinx.coroutines.Dispatchers
import javax.inject.Inject

class LoginProcessor @Inject constructor(
    private val database: UNESDatabase
) {
    suspend fun process(username: String, password: String, institution: String, markSelected: Boolean = false): LoginCallback {
        SagresNavigator.instance.logout()
        SagresNavigator.instance.setSelectedInstitution(institution)
        val result = SagresNavigator.instance.login(username, password)
        if (result.status == Status.SUCCESS) {
            updateOrCreateAccess(username, password, institution, markSelected)
        }
        return result
    }

    private suspend fun updateOrCreateAccess(username: String, password: String, institution: String, markSelected: Boolean) {
        val current = database.credentials().getCredential(username, institution)
        if (current == null) {
            database.credentials().insert(Credential(0, username, password, institution, false))
        } else if (current.password != password) {
            database.credentials().changePassword(username, password, institution)
        }
        database.credentials().markValid(username, institution, true)
        if (markSelected) database.credentials().select(username, institution)
    }
}