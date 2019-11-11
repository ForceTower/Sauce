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

package dev.forcetower.database

import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy

abstract class AbstractDao<T> {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun insert(item: T): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun insertAll(items: List<T>): List<Long>

    @Insert(onConflict = OnConflictStrategy.ABORT)
    abstract suspend fun insertAborting(item: T): Long

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    abstract suspend fun insertIgnore(item: T): Long

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    abstract suspend fun insertAllIgnoring(items: List<T>): List<Long>

    @Delete
    abstract suspend fun delete(item: T): Int

    @Delete
    abstract suspend fun deleteAll(items: List<T>): Int
}