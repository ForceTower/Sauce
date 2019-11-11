package dev.forcetower.unes

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.os.Message
import android.os.Messenger
import android.view.View
import androidx.core.os.bundleOf
import androidx.core.view.updatePadding
import androidx.databinding.DataBindingUtil
import androidx.navigation.findNavController
import com.google.android.material.snackbar.Snackbar
import dev.forcetower.core.base.BaseDaggerActivity
import dev.forcetower.core.extensions.doOnApplyWindowInsets
import dev.forcetower.core.service.ISyncService
import dev.forcetower.core.service.ISyncServiceCallback
import dev.forcetower.core.service.SynchronizationService
import dev.forcetower.unes.databinding.ActivityMainBinding
import timber.log.Timber

class MainActivity : BaseDaggerActivity() {
    private lateinit var binding: ActivityMainBinding
    private var service: ISyncService? = null
    private var bound = false

    //TODO Remember, this callbacks are not main threaded
    private val callback = object : ISyncServiceCallback.Stub() {
        override fun onLoginSuccessful(name: String?) {
            Timber.d("Service says that the user $name is connected... should we do something?")
        }

        override fun onLoginFailed(reason: Int, message: String?) {

        }
    }

    private val connection = object: ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, binder: IBinder?) {
            Timber.d("Connected to the service $name")
            service = ISyncService.Stub.asInterface(binder)
            service?.registerCallback(callback)
            bound = true
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            Timber.d("Disconnected from the service... $name")
            service = null
            bound = false
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        binding.rootView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_STABLE or
                View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or
                View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION

        binding.root.doOnApplyWindowInsets { v, insets, padding ->
            v.updatePadding(top = padding.top + insets.systemWindowInsetTop)
        }

        Handler(Looper.getMainLooper()).let { looper ->
            looper.postDelayed({
                Timber.d("Start send first")
                sendSyncSignal()
                Timber.d("Sent first")
            }, 2000)

            looper.postDelayed({
                Timber.d("Start send second")
                sendStopSyncSignal()
                Timber.d("Sent second")
            }, 2010)
        }
    }

    override fun onSupportNavigateUp() = findNavController(R.id.fragment_container).navigateUp()

    override fun showSnack(string: String, duration: Int) {
        getSnackInstance(string, duration)?.show()
    }

    override fun getSnackInstance(string: String, duration: Int): Snackbar? {
        return Snackbar.make(binding.rootView, string, duration)
    }

    override fun onStart() {
        super.onStart()
        Intent(this, SynchronizationService::class.java).also { intent ->
            bindService(intent, connection, Context.BIND_AUTO_CREATE)
        }
    }

    override fun onStop() {
        super.onStop()
        if (bound) {
            unbindService(connection)
            bound = false
        }
    }

    private fun sendSyncSignal() {
        if (!bound) return
        service?.startSync()
    }

    private fun sendStopSyncSignal() {
        if (!bound) return
        service?.stopSync()
    }
}
