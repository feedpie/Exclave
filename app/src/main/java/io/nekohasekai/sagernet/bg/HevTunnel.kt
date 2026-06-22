package io.nekohasekai.sagernet.bg

import io.nekohasekai.sagernet.ktx.Logs
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.Closeable
import java.io.File

object HevTunnel {
    private const val TAG = "HevTunnel"

    init {
        try {
            System.loadLibrary("hev-socks5-tunnel")
        } catch (e: UnsatisfiedLinkError) {
            Logs.w(TAG, "Failed to load hev-socks5-tunnel library", e)
        }
    }

    external fun TProxyStartService(configPath: String, fd: Int)
    external fun TProxyStopService()
    external fun TProxyGetStats(): LongArray

    fun start(configYaml: String, tunFd: Int, dataDir: String): HevTunnelHandle {
        val file = File(dataDir, "hev-tunnel.yml")
        file.parentFile?.mkdirs()
        file.writeText(configYaml)
        TProxyStartService(file.absolutePath, tunFd)
        return HevTunnelHandle(file)
    }
}

class HevTunnelHandle(private val configFile: File) : Closeable {
    override fun close() {
        GlobalScope.launch(Dispatchers.IO) {
            HevTunnel.TProxyStopService()
        }
        runCatching { configFile.delete() }
    }

    fun stats(): TrafficStats {
        val data = HevTunnel.TProxyGetStats()
        return TrafficStats(
            txPackets = data[0],
            txBytes = data[1],
            rxPackets = data[2],
            rxBytes = data[3]
        )
    }
}

data class TrafficStats(
    val txPackets: Long,
    val txBytes: Long,
    val rxPackets: Long,
    val rxBytes: Long,
)
