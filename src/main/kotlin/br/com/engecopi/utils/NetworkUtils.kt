package br.com.engecopi.utils

import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.net.InetAddress
import java.net.InetSocketAddress
import java.net.Socket

object NetworkUtils {
  fun isHostReachable(hostname: String?): Boolean {
    //hostname ?: return false
    //return pingNet(hostname)
    return true
  }
  
  private fun pingCmd(hostname: String?): Boolean {
    val p1 =
      Runtime.getRuntime()
        .exec("ping -c 1 $hostname")
    val output = StringBuilder()
    val reader = BufferedReader(
      InputStreamReader(p1.getInputStream()))
    var line: String
    while(reader.readLine().also {line = it} != null) {
      output.append(line + "\n")
    }
    val returnVal = p1.waitFor()
    val reachable = returnVal == 0
    return reachable
  }
  
  private fun pingSsh(hostname: String?): Boolean {
    return true
  }
  
  private fun pingNet(hostname: String?): Boolean {
    hostname ?: return false
    val byteAddr =
      hostname.split(".")
        .mapNotNull {
          it.toIntOrNull()
            ?.toByte()
        }
        .toByteArray()
    val inet = InetAddress.getByAddress(byteAddr)
    val isReachable = inet.isReachable(10000)
    return isReachable
  }
  
  private fun isReachable(addr: String,
                          openPort: Int,
                          timeOutMillis: Int): Boolean { // Any Open port on other machine
    // openPort =  22 - ssh, 80 or 443 - webserver, 25 - mailserver etc.
    return try {
      Socket().use {soc -> soc.connect(InetSocketAddress(addr, openPort), timeOutMillis)}
      true
    } catch(ex: IOException) {
      false
    }
  }
}