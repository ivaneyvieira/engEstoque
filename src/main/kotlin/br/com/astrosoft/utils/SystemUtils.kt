package br.com.astrosoft.utils

import org.imgscalr.Scalr
import org.imgscalr.Scalr.Method
import org.imgscalr.Scalr.Mode
import java.awt.image.BufferedImage
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.nio.charset.Charset
import java.nio.file.Files
import java.nio.file.Paths
import java.security.MessageDigest
import javax.imageio.ImageIO

object SystemUtils {
  private val enviroment = System.getenv()
  fun variable(variable: String, def: String): String {
    val envResult = SystemUtils.enviroment[variable]
    return if(envResult == null || envResult.trim {it <= ' '} == "") {
      def
    }
    else envResult
  }
  
  fun resize(imagem: ByteArray?, width: Int, height: Int): ByteArray? {
    return try {
      if(imagem == null) return null
      val bImagemIn = toBufferedImage(imagem) ?: return null
      val bimage = Scalr.resize(bImagemIn, Method.QUALITY, Mode.FIT_TO_WIDTH, width, height)
      bimage.toByteArray()
    } catch(e: IOException) {
      ByteArray(0)
    }
  }
  
  @Throws(IOException::class)
  private fun toBufferedImage(imagem: ByteArray?): BufferedImage? {
    if(imagem == null) return null
    val inputStream = ByteArrayInputStream(imagem)
    return ImageIO.read(inputStream)
  }
  
  @Throws(IOException::class)
  private fun BufferedImage.toByteArray(): ByteArray? {
    val baos = ByteArrayOutputStream()
    ImageIO.write(this, "jpg", baos)
    baos.flush()
    val imageInByte = baos.toByteArray()
    baos.close()
    return imageInByte
  }
  
  private fun hashString(type: String, input: String): String {
    val hexChar = "0123456789ABCDEF"
    val bytes = MessageDigest.getInstance(type)
      .digest(input.toByteArray())
    val result = StringBuilder(bytes.size * 2)
    
    bytes.forEach {
      val i = it.toInt()
      result.append(hexChar[i shr 4 and 0x0f])
      result.append(hexChar[i and 0x0f])
    }
    
    return result.toString()
  }
  
  fun md5(text: String): String {
    return hashString("MD5", text)
  }
}

class FileText(private val filename: String) {
  @Throws(IOException::class)
  private fun readFile(): String? {
    return if(filename.startsWith('/')) readFile(Charset.defaultCharset())
    else filename
  }
  
  @Throws(IOException::class)
  private fun readFile(encoding: Charset): String {
    val resource = SystemUtils::class.java.getResource(filename)
    val path = Paths.get(resource.toURI())
    val encoded = Files.readAllBytes(path)
    return String(encoded, encoding)
  }
  
  override fun toString(): String {
    return readFile() ?: ""
  }
}

