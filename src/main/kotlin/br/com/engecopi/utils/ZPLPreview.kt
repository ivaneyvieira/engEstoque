package br.com.engecopi.utils

import khttp.extensions.fileLike
import khttp.post

object ZPLPreview {
  fun createPdf(zpl: String, size: String): ByteArray? {
    val files = listOf(zpl.fileLike("file"))
    val headers = mapOf("Accept" to "application/pdf")
    val url = "http://api.labelary.com/v1/printers/8dpmm/labels/$size/"
    val response = post(url = url, headers = headers, files = files, stream = true)
    return if(response.statusCode == 200) response.content
    else return null
  }
}