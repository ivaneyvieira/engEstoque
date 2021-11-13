package br.com.engecopi.framework.ui.view

import com.github.mvysny.karibudsl.v8.AutoView

private const val VIEW_NAME_USE_DEFAULT = "USE_DEFAULT"

internal fun <T: Annotation> Class<*>.findAnnotation(ac: Class<T>): T? {
  // unfortunately, the class discovery algorithm of ServletContainerInitializer+@HandlesTypes is quite weak.
  // it will not handle transitive annotations; for example it will not handle classes implementing interfaces annotated with @AutoView
  // therefore, we'll just use the getAnnotation()
  return getAnnotation(ac)
}

private fun String.upperCamelToLowerHyphen(): String {
  val sb = StringBuilder()
  for(i in this.indices) {
    var c = this[i]
    if(Character.isUpperCase(c)) {
      c = Character.toLowerCase(c)
      if(shouldPrependHyphen(i)) {
        sb.append('-')
      }
    }
    sb.append(c)
  }
  return sb.toString()
}

private fun String.shouldPrependHyphen(i: Int): Boolean = if(i == 0) {
  // Never put a hyphen at the beginning
  false
}
else if(!Character.isUpperCase(this[i - 1])) {
  // Append if previous char wasn't upper case
  true
}
else i + 1 < this.length && !Character.isUpperCase(this[i + 1])

fun Class<*>.toViewName(): String {
  val annotation = requireNotNull(findAnnotation(AutoView::class.java)) {"Missing @AutoView annotation on $this"}
  val name = annotation.value
  return if(name == VIEW_NAME_USE_DEFAULT) simpleName.removeSuffix("View").upperCamelToLowerHyphen() else name
}


