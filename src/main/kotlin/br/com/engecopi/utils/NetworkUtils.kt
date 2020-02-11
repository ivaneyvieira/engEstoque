package br.com.engecopi.utils

import java.net.InetAddress

object NetworkUtils {
  fun isHostReachable(hostname: String?): Boolean {
    hostname ?: return false
    val inet = InetAddress.getByName(hostname)
    return inet.isReachable(2000)
  }
}