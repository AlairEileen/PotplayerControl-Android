package space.alair.potplayer_control.presenters

import android.app.Activity
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import space.alair.potplayer_control.App
import space.alair.potplayer_control.net.RemoteConnector

import java.util.HashMap
import kotlin.coroutines.CoroutineContext

class RemotePresenter private constructor() {
    private val remoteConnector by lazy {
        RemoteConnector().apply {
            onError = {

            }
            onReceivedMsg = {
                msgMap["SocketRcvMSg"] = it
                msgMap["SocketRcvflag"] = "yes"
            }


        }
    }

    fun connect() {
        remoteConnector.startSocket()
    }

    private val msgMap = HashMap<String, Any>()
    operator fun get(key: String): Any? {
        return msgMap[key]
    }

    companion object {
        val instance: RemotePresenter by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) { RemotePresenter() }
    }

    fun sendMessage(playerCmd: PlayerCmdText) {
        remoteConnector.sendMessage(playerCmd.cmd)
        App.app?.vibratorDefault()
    }


    fun close() {
        remoteConnector.close()
    }

}

enum class PlayerCmdText(val cmd: String) {

    /**
     * 播放暂停
     */
    PAUSE("pause"),

    /**
     * 静音
     */
    MUTE("mute"),

    NEXT("next"),
    PREVIOUS("prev"),
    VOLUME_DOWN("vol-"),
    VOLUME_UP("vol+"),
    FULL_SCREEN("full"),
    FORWARD("fastprev"),
    REWIND("fastnext"),
    EXIT_PC("exitp"),
    REFRESH("refr"),
    SHUTDOWN_PC("shut"),


}