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

package dev.forcetower.model.base

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.ForeignKey.CASCADE
import androidx.room.Ignore
import androidx.room.Index
import androidx.room.PrimaryKey
import java.util.UUID

/**
 * This puts together information about the extracted account from Sagres
 */
@Entity(indices = [
    Index(value = ["uuid"], unique = true),
    Index(value = ["credentialId"], unique = true)
], foreignKeys = [
    ForeignKey(childColumns = ["credentialId"], parentColumns = ["uid"], entity = Credential::class, onDelete = CASCADE, onUpdate = CASCADE)
])
data class Profile (
    @PrimaryKey(autoGenerate = true)
    val uid: Long,
    val processedName: String?,
    val username: String,
    val editedName: String?,
    val credentialId: Long,

    val email: String? = null,
    val courseId: Long? = null,
    val course: String? = null,
    val institutionId: String? = null,

    @ColumnInfo(defaultValue = "-1")
    val score: Double = -1.0,
    @ColumnInfo(defaultValue = "-1")
    val calculatedScore: Double = -1.0,

    val profilePictureUrl: String? = null,
    @ColumnInfo(defaultValue = "false")
    val darkThemeEnabled: Boolean = false,
    @ColumnInfo(defaultValue = "0")
    val darkThemeInvites: Int = 0,
    @ColumnInfo(defaultValue = "0")
    val experimentsFlags: Long = 0,

    val uuid: String = UUID.randomUUID().toString()
) {
    @Ignore
    val name: String = editedName ?: processedName ?: username

    companion object {
        const val COLLECTION = "profiles"
    }
}