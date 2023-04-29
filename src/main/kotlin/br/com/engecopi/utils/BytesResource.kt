package br.com.engecopi.utils

import com.vaadin.server.StreamResource
import com.vaadin.server.StreamResource.StreamSource
import java.io.ByteArrayInputStream
import java.io.InputStream

fun ByteArray.makeResource(): StreamResource {
  val nome = System.currentTimeMillis().toString()
  val imagesource = BytesStreamSource(this)
  val streamResource = StreamResource(imagesource, "$nome.pdf")
  streamResource.cacheTime = 1000
  streamResource.mimeType = "application/pdf"
  streamResource.stream.setParameter("Content-Disposition", "attachment; filename=$nome.pdf")

  return streamResource
}

class BytesStreamSource(private val imagem: ByteArray) : StreamSource {
  override fun getStream(): InputStream {
    return ByteArrayInputStream(imagem)
  }
}