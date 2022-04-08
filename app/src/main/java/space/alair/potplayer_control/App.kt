package space.alair.potplayer_control

import android.app.Activity
import android.app.Application
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager

class App : Application() {
    override fun onCreate() {
        super.onCreate()
        app = this
    }

    companion object {
        var app: App? = null
    }

    val vibrator by lazy {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            (getSystemService(Activity.VIBRATOR_MANAGER_SERVICE) as VibratorManager).defaultVibrator
        } else {
            @Suppress("DEPRECATION")
            getSystemService(Activity.VIBRATOR_SERVICE) as Vibrator
        }
    }

    fun vibratorDefault() {
        vibrator.vibrate(
            VibrationEffect.createOneShot(
                20L,
                VibrationEffect.DEFAULT_AMPLITUDE
            )
        )
    }
}