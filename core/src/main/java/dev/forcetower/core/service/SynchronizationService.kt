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

package dev.forcetower.core.service

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.os.RemoteCallbackList
import dagger.android.DaggerService
import dev.forcetower.database.base.UNESDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import okhttp3.Dispatcher
import timber.log.Timber
import java.util.concurrent.Executors
import javax.inject.Inject

class SynchronizationService : Service() {
    private val callbacks = RemoteCallbackList<ISyncServiceCallback>()

    private val binder = object: ISyncService.Stub() {
        override fun startSync() {
            Timber.d("Sync started")
            sync()
        }

        override fun stopSync() {
            Timber.d("Stop sync!")
            stopSelf()
        }

        override fun registerCallback(callback: ISyncServiceCallback?) {
            callback?.let {
                Timber.d("Registers a new callback")
                callbacks.register(it)
            }
        }

        override fun unregisterCallback(callback: ISyncServiceCallback?) {
            callback?.let {
                Timber.d("Unregisters a callback")
                callbacks.unregister(it)
            }
        }
    }

    override fun onBind(intent: Intent): IBinder {
        return binder
    }

    private fun sync() {
        // TODO Remember that this is IPC, the caller will be stopped for long running tasks
        GlobalScope.launch(Dispatchers.IO) {
            Timber.d("Executing...")
            val broadcast = callbacks.beginBroadcast()
            Timber.d("Broadcast: $broadcast")
            callbacks.getBroadcastItem(0).onLoginSuccessful("Testing Fox")
            callbacks.finishBroadcast()
            var i: Long = 0
            while (i < 100000000000) {
                i++
            }
            Timber.d("Completed sync")
        }
    }

}
