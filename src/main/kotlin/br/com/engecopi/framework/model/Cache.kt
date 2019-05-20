package br.com.engecopi.framework.model

import kotlin.reflect.KProperty

val mapCache = mutableMapOf<String, Any?>()

open class Cache<T>(val initializer: () -> T?) {
  private var millisecond = System.currentTimeMillis()

  protected fun composeKey(thisRef: Any?, property: KProperty<*>): String {
    val classe = thisRef?.javaClass?.simpleName
    return if(thisRef is BaseModel) {
      val id = thisRef.id
      "$classe:$id:${property.name}"
    }
    else {
      "$classe:${property.name}"
    }
  }

  private fun calculeDelay(): Long {
    val currentTimeMillis = System.currentTimeMillis()
    val delay = currentTimeMillis - millisecond
    millisecond = System.currentTimeMillis()
    return delay
  }

  protected fun getValue(key: String): T? {
    val delay = calculeDelay()

    return if(delay < 30000 && mapCache.containsKey(key)) {
      @Suppress("UNCHECKED_CAST") mapCache[key] as? T
    }
    else {
      val value = initializer()
      mapCache[key] = value
      value
    }
  }
}

class DelegadeCacheValue<T>(initializer: () -> T?): Cache<T>(initializer) {
  operator fun getValue(thisRef: Any?, property: KProperty<*>): T? {
    val key = composeKey(thisRef, property)
    return getValue(key)
  }
}

class DelegadeCacheList<T>(initializer: () -> List<T>): Cache<List<T>>(initializer) {
  operator fun getValue(thisRef: Any?, property: KProperty<*>): List<T> {
    val key = composeKey(thisRef, property)
    return getValue(key).orEmpty()
  }
}
