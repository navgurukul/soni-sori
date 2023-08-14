package org.merakilearn.util.webide.net

import eu.bitwalker.useragentutils.UserAgent
import timber.log.Timber
import java.net.Inet4Address
import java.net.InetAddress
import java.net.NetworkInterface
import java.net.SocketException
import java.util.*

object NetworkUtils {

    var server: HyperServer? = null

    val ipAddress: String?
        get() {
            try {
                val en = NetworkInterface.getNetworkInterfaces()
                while (en.hasMoreElements()) {
                    val intf = en.nextElement() as NetworkInterface
                    val enumIpAddr = intf.inetAddresses
                    while (enumIpAddr.hasMoreElements()) {
                        val inetAddress = enumIpAddr.nextElement() as InetAddress
                        if (!inetAddress.isLoopbackAddress && inetAddress is Inet4Address) {
                            return inetAddress.getHostAddress()
                        }
                    }
                }
            } catch (e: SocketException) {
                Timber.e(e)
            }

            return null
        }

    fun parseUA(ua: String): String {
        val agent = UserAgent.parseUserAgentString(ua)
        return agent.operatingSystem.getName() + " / " + agent.browser.getName() + " " + agent.browserVersion.version
    }

    fun parseUAList(uaList: LinkedList<String>): LinkedList<String> =
        uaList.mapTo(LinkedList()) { parseUA(it) }
}