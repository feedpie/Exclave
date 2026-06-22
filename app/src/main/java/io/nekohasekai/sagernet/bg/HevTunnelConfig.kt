package io.nekohasekai.sagernet.bg

import io.nekohasekai.sagernet.database.DataStore

fun buildHevConfig(
    socksPort: Int,
    mtu: Int = DataStore.mtu,
    ipv4Address: String = VpnService.PRIVATE_VLAN4_CLIENT,
    ipv6Address: String? = VpnService.PRIVATE_VLAN6_CLIENT.takeIf { DataStore.enableVPNInterfaceIPv6Address },
): String = buildString {
    appendLine("tunnel:")
    appendLine("  name: 'tun0'")
    appendLine("  mtu: $mtu")
    appendLine("  multi-queue: true")
    appendLine("  ipv4: '$ipv4Address'")
    if (ipv6Address != null) {
        appendLine("  ipv6: '$ipv6Address'")
    }
    appendLine("socks5:")
    appendLine("  port: $socksPort")
    appendLine("  address: '127.0.0.1'")
    appendLine("  udp: 'udp'")
    appendLine("  pipeline: true")
}
