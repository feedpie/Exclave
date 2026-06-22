package io.nekohasekai.sagernet.bg

import io.nekohasekai.sagernet.ktx.Logs
import java.io.Closeable
import java.io.File

object HevTunnel {
    private const val TAG = "HevTunnel"

    @Volatile
    internal var loaded = false

    init {
        try {
            System.loadLibrary("hev-socks5-tunnel")
            loaded = true
        } catch (e: UnsatisfiedLinkError) {
            Logs.w(TAG, "Failed to load hev-socks5-tunnel library", e)
        }
    }

    external fun TProxyStartService(configPath: String, fd: Int)
    external fun TProxyStopService()
    external fun TProxyGetStats(): LongArray

    fun start(configYaml: String, tunFd: Int, dataDir: String): HevTunnelHandle {
        check(loaded) { "hev-socks5-tunnel library not loaded" }
        val file = File(dataDir, "hev-tunnel.yml")
        file.parentFile?.mkdirs()
        file.writeText(configYaml)
        TProxyStartService(file.absolutePath, tunFd)
        return HevTunnelHandle(file)
    }
}

class HevTunnelHandle(private val configFile: File) : Closeable {
    override fun close() {
        if (HevTunnel.loaded) {
            runCatching {
                HevTunnel.TProxyStopService()
            }
        }
        runCatching { configFile.delete() }
    }

    fun stats(): TrafficStats {
        if (!HevTunnel.loaded) return TrafficStats(0, 0, 0, 0)
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
