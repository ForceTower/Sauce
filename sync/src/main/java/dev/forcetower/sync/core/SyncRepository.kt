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

package dev.forcetower.sync.core

import android.content.SharedPreferences
import com.forcetower.sagres.SagresNavigator
import com.forcetower.sagres.database.model.SagresDisciplineClassLocation
import com.forcetower.sagres.database.model.SagresMessage
import com.forcetower.sagres.operation.BaseCallback
import com.forcetower.sagres.operation.Status
import com.forcetower.sagres.parsers.SagresBasicParser
import com.forcetower.sagres.parsers.SagresMessageParser
import com.forcetower.sagres.parsers.SagresScheduleParser
import dev.forcetower.database.base.UNESDatabase
import dev.forcetower.model.base.Credential
import dev.forcetower.model.base.Message
import dev.forcetower.sync.LoginProcessor
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import org.jsoup.nodes.Document
import timber.log.Timber
import java.util.Calendar
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SyncRepository @Inject constructor(
    private val database: UNESDatabase,
    private val loginEngine: LoginProcessor,
    private val preferences: SharedPreferences
) {

    suspend fun execute(notify: Boolean) {
        Mutex(false).withLock {
            perform(notify)
        }
    }

    private suspend fun perform(notify: Boolean = true) {
        if (!isSyncingAllowed()) return
        val credentials = database.credentials().getAll()
        credentials.forEach { credential ->
            val registry = onPreExecute()
            execute(credential, registry, notify)
            onPostExecute(registry)
        }
    }

    private suspend fun isSyncingAllowed(): Boolean {
        return true
    }

    private suspend fun onPreExecute(): Any {
        return Any()
    }

    private suspend fun onPostExecute(registry: Any) {

    }

    private suspend fun execute(
        credential: Credential,
        registry: Any,
        notify: Boolean
    ): Int {
        val home = login(credential) ?: TODO("Task complete: Login failed - White stuff down and terminate")

        val score = SagresBasicParser.getScore(home)
        val name = SagresBasicParser.getName(home)
        val schedule = SagresScheduleParser.getSchedule(home)
        val messages = SagresMessageParser.getMessages(home)

        updateProfile(name, score, credential)
        defineMessages(messages, credential, notify, true)

        if (shouldDisciplinesSync()) disciplines(credential)
        startPage(schedule, credential)
        grades(credential)
        servicesRequested(credential)
        return 0
    }

    private suspend fun login(credential: Credential): Document? {
        val callback = loginEngine.process(credential.username, credential.password, credential.institution)
        when (callback.status) {
            Status.SUCCESS -> return callback.document
            Status.INVALID_LOGIN -> {
                if (callback.code == 401)
                    onInvalidLogin(credential)
            }
            else -> produceErrorMessage(callback)
        }
        return null
    }

    private suspend fun startPage(homeSchedule: List<SagresDisciplineClassLocation>?, credential: Credential): Boolean {
        val start = SagresNavigator.instance.startPage()
        return when (start.status) {
            Status.SUCCESS -> {
                defineCalendar(start.calendar)
                defineDisciplines(start.disciplines)
                defineDisciplineGroups(start.groups)
                defineSchedule(start.locations ?: homeSchedule, credential)
                defineDemand(start.isDemandOpen)

                Timber.d("Semesters: ${start.semesters}")
                Timber.d("Disciplines: ${start.disciplines}")
                Timber.d("Groups: ${start.groups}")
                Timber.d("Calendar: ${start.calendar}")
                true
            }
            else -> {
                produceErrorMessage(start)
                false
            }
        }
    }

    private suspend fun grades(credential: Credential) {

    }

    private suspend fun servicesRequested(credential: Credential) {

    }

    private suspend fun disciplines(credential: Credential) {

    }

    private fun shouldDisciplinesSync(): Boolean {
        val calendar = Calendar.getInstance()
        val today = calendar.get(Calendar.DAY_OF_MONTH)

        val dailyDisciplines = preferences.getString("stg_daily_discipline_sync", "2")?.toIntOrNull() ?: 2
        val currentDaily = preferences.getInt("daily_discipline_count", 0)
        val currentDayDiscipline = preferences.getInt("daily_discipline_day", -1)
        val lastDailyHour = preferences.getInt("daily_discipline_hour", 0)

        val isNewDaily = currentDayDiscipline != today || dailyDisciplines == -1
        val currentDailyHour = calendar.get(Calendar.HOUR_OF_DAY)

        val (actualDailyCount, nextHour) = if (isNewDaily)
            0 to -1
        else
            currentDaily to if (lastDailyHour < 8) 10 else lastDailyHour + 4

        return ((actualDailyCount < dailyDisciplines) || (dailyDisciplines == -1)) &&
                (currentDailyHour >= nextHour)
    }

    private suspend fun updateProfile(name: String?, score: Double, credential: Credential) {
        database.profiles().updateProfileInfo(name, score, credential)
    }

    private suspend fun defineSchedule(locations: List<SagresDisciplineClassLocation>?, credential: Credential) {
        locations ?: return
    }

    private suspend fun defineMessages(messages: List<SagresMessage>, credential: Credential, notify: Boolean, needsReversing: Boolean = false) {
        if (needsReversing) {
            val millis = System.currentTimeMillis()
            messages.reversed().forEachIndexed { index, message ->
                message.processingTime = millis + index
            }
        }
        database.messages().prepare(messages.map { Message.fromMessage(it, !notify, credential.uid) })
    }

    private suspend fun onInvalidLogin(credential: Credential) {
        Timber.d("Invalid credentials... Returning")
        database.credentials().changeValidation(credential.uid, false)
    }

    private fun produceErrorMessage(callback: BaseCallback<*>) {
        Timber.i("Failed executing with status ${callback.status} and throwable message [${callback.throwable?.message}]")
    }
}