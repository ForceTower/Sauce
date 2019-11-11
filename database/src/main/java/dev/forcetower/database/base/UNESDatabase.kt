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

package dev.forcetower.database.base

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import dev.forcetower.database.base.dao.CredentialDao
import dev.forcetower.model.base.Credential

@Database(entities = [
    Credential::class
], version = 1, exportSchema = true)
abstract class UNESDatabase : RoomDatabase() {
    abstract fun credentials(): CredentialDao

    companion object {
        private const val DATABASE_NAME = "unes-shared-db"
        @Volatile private var instance: UNESDatabase? = null

        fun getInstance(context: Context): UNESDatabase {
            return instance ?: synchronized(this) {
                instance ?: buildDatabase(context).also { instance = it }
            }
        }

        private fun buildDatabase(context: Context): UNESDatabase {
            return Room.databaseBuilder(context, UNESDatabase::class.java, DATABASE_NAME)
                .enableMultiInstanceInvalidation()
                .build()
        }
    }
}