package br.com.engecopi.utils

import org.glassfish.jersey.media.multipart.MultiPartFeature
import javax.ws.rs.client.ClientBuilder
import javax.ws.rs.client.Entity
import javax.ws.rs.core.MediaType

object ZPLPreview {
  fun createImage(zpl: String, size : String): ByteArray? {
    val client = ClientBuilder.newBuilder()
      .register(MultiPartFeature::class.java)
      .build()
    val target = client.target("http://api.labelary.com/v1/printers/8dpmm/labels/$size/")
    val request = target.request()
    request.accept("application/pdf")
    val response = request.post(Entity.entity(zpl, MediaType.APPLICATION_FORM_URLENCODED))

    return if(response.status == 200)
      response.readEntity(ByteArray::class.java)
    else
      null
  }
}