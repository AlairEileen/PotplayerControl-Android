package space.alair.potplayer_control.net

import android.os.Build
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.*
import java.net.InetAddress
import java.net.InetSocketAddress
import java.net.Socket
import java.net.UnknownHostException
import java.nio.charset.Charset
import java.util.*

class RemoteConnector() {
    private val defaultPort = 5005
    private var passwordvalue: String? = ""
    private val playType = "<potplayer>"
    private val timeout = 3000
    val dataBuffer by lazy { ByteArray(10240) }
    private val defaultHost = "1.1.1.157"
    private var socket: Socket? = null
    private var socketOS: PrintWriter? = null
    private var socketIS: InputStream? = null
    private var isOpen = true

    var onError: (() -> Unit)? = null
    var onReceivedMsg: ((msg: String) -> Unit)? = null

    fun startSocket() {
        GlobalScope.launch(Dispatchers.IO) {
            socket = Socket()
            try {
                socket?.connect(getAddress(), timeout)
                socketIS = socket?.getInputStream()
                socketOS = PrintWriter(
                    BufferedWriter(
                        OutputStreamWriter(
                            socket?.getOutputStream()
                        )
                    ) as Writer, true
                )
                while (isOpen) {
                    listenMsg()
                    Thread.sleep(400)
                }
            } catch (e: UnknownHostException) {
                e.printStackTrace()

            } catch (e: Exception) {
                e.printStackTrace()
                onError?.invoke()
            }
        }
    }

    private fun listenMsg() {
        val sb = StringBuffer()
        var readFlag = 1
        while (readFlag > 0) {
            Arrays.fill(dataBuffer, 0.toByte())
            val readSize = socketIS?.read(dataBuffer) ?: 0
            if (readSize == -1) {
                readFlag = 0
//                inputStream?.close()
            }
            if (readSize != 0 && readSize > 0) {
                val temMsg = String(dataBuffer)
                sb.append(String(dataBuffer, 0, readSize))
                readFlag = if (temMsg.indexOf("Welcome to TVPS") == -1) {
                    1
                } else {
                    0
                    //                    inputStream?.close()
                }
            }
        }
        val msg = sb.toString()
        if (msg.isNotEmpty()) {
            val mySocketRcvMSg = String(msg.toByteArray(), Charset.forName("utf-8"))
            onReceivedMsg?.invoke(mySocketRcvMSg)
        }
        sendMessage("heart")
    }

    fun sendMessage(message: String) {
        GlobalScope.launch(Dispatchers.IO) {
            try {
                socketOS?.println(message + "<hid>" + passwordvalue + Build.MODEL + "</hid>" + playType)
//                out.close()
            } catch (e: Exception) {
                e.printStackTrace()
                onError?.invoke()
            }
        }
    }


    private fun getAddress(): InetSocketAddress {
        return InetSocketAddress(InetAddress.getByName(defaultHost), defaultPort)
    }

    fun close() {
        GlobalScope.launch(Dispatchers.IO) {
            isOpen = false
            try {
                socketIS?.close()
                socketOS?.close()
                socket?.close()
            } catch (e: Exception) {

            }
        }
    }

}