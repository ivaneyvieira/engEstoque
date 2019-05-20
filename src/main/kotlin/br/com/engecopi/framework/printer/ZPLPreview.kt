package br.com.engecopi.framework.printer

import org.glassfish.jersey.media.multipart.MultiPartFeature
import javax.ws.rs.client.ClientBuilder
import javax.ws.rs.client.Entity
import javax.ws.rs.core.MediaType

object ZPLPreview {
  fun createImage(zpl: String, pagina: Int = 0): ByteArray? {
    val client =
      ClientBuilder.newBuilder()
        .register(MultiPartFeature::class.java)
        .build()
    // adjust print density (8dpmm), label width (4 inches), label height (6 inches), and label index (0) as necessary
    val target = client.target("http://api.labelary.com/v1/printers/8dpmm/labels/4x4/$pagina/")
    val request = target.request()
    val response = request.post(Entity.entity(zpl, MediaType.APPLICATION_FORM_URLENCODED))

    return if(response.status == 200) {
      response.readEntity(ByteArray::class.java)
    }
    else {
      null
    }
  }
}