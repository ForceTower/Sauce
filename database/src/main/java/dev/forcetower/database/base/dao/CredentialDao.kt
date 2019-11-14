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

package dev.forcetower.database.base.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import dev.forcetower.database.AbstractDao
import dev.forcetower.model.base.Credential

@Dao
abstract class CredentialDao : AbstractDao<Credential>() {
    @Query("SELECT * FROM Credential")
    abstract suspend fun getAll(): List<Credential>

    @Query("SELECT * FROM Credential WHERE selected = 1")
    abstract suspend fun getActiveCredential(): Credential?

    @Query("SELECT * FROM Credential WHERE username = :username AND institution = :institution LIMIT 1")
    abstract suspend fun getCredential(username: String, institution: String): Credential?

    @Query("SELECT * FROM Credential WHERE uid = :uid")
    abstract suspend fun getCredential(uid: Long): Credential?

    @Query("UPDATE Credential SET selected = 0 WHERE selected = 1")
    protected abstract suspend fun markAllNotSelected()

    @Query("UPDATE Credential SET selected = 1 WHERE uid = :uid")
    protected abstract suspend fun markSelected(uid: Long)

    @Transaction
    open suspend fun select(username: String, institution: String) {
        val credential = getCredential(username, institution)
        if (credential != null) {
            markAllNotSelected()
            markSelected(credential.uid)
        }
    }

    @Transaction
    open suspend fun select(uid: Long) {
        markAllNotSelected()
        markSelected(uid)
    }

    @Query("DELETE FROM Credential")
    abstract suspend fun deleteAll()

    @Query("UPDATE Credential SET password = :password WHERE username = :username AND institution = :institution")
    abstract fun changePassword(username: String, password: String, institution: String)

    @Query("UPDATE Credential SET valid = :valid WHERE username = :username AND institution = :institution")
    abstract fun markValid(username: String, institution: String, valid: Boolean)
}