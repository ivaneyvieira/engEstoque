package br.com.engecopi.utils

fun String?.lpad(size: Int, filler: String): String {
  var str = this ?: ""
  if(str.length > size) return str.substring(0, size)
  val buf = StringBuilder(str)
  while(buf.length < size) buf.insert(0, filler)

  str = buf.toString()
  return str
}

fun String?.rpad(size: Int, filler: String): String {
  val str = this ?: ""
  if(str.length > size) return str.substring(0, size)
  val buf = StringBuilder(str)
  while(buf.length < size) buf.append(filler)

  return buf.toString()
}

fun String?.trimNull(): String {
  return this?.trim {it <= ' '} ?: ""
}

fun String.mid(start: Int, len: Int): String {
  return if(this == "") ""
  else {
    val end = start + len
    val pStart = when {
      start < 0       -> 0
      start >= length -> length - 1
      else            -> start
    }
    val pEnd = when {
      end < 0      -> 0
      end > length -> length
      else         -> end
    }
    if(pStart <= pEnd) substring(pStart, pEnd)
    else ""
  }
}

fun String.mid(start: Int): String {
  return mid(start, start + length)
}
