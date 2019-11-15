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
import dev.forcetower.model.base.Profile

@Dao
abstract class ProfileDao : AbstractDao<Profile>() {
    @Query("UPDATE Profile SET processedName = :name WHERE credentialId = :credentialId")
    abstract suspend fun updateProcessedName(name: String?, credentialId: Long)

    @Query("UPDATE Profile SET score = :score WHERE credentialId = :credentialId")
    abstract suspend fun updateScore(score: Double, credentialId: Long)

    @Query("SELECT * FROM Profile WHERE credentialId = :credentialId")
    abstract suspend fun getProfile(credentialId: Long): Profile?

    @Transaction
    open suspend fun updateProfileInfo(name: String?, score: Double, credential: Credential) {
        val credentialId = credential.uid
        val profile = getProfile(credentialId)
        if (profile == null) {
            val generated = Profile(0, name, credential.username, null, credentialId)
            insert(generated)
        } else {
            updateProcessedName(name, credentialId)
            if (score >= 0) updateScore(score, credentialId)
        }
    }
}