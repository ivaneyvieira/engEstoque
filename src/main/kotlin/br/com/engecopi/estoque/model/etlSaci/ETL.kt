package br.com.engecopi.estoque.model.etlSaci

abstract class ETL<T: EntryID>(val source: List<T>, val target: List<T>) {
  var listenerInsert: (T) -> Unit = {}
  fun execute() {
    val idsTarget = target.map {it.id}
    val idsSource = source.map {it.id}
    val idsInsert = idsSource.minus(idsTarget)
    val idsDelete = idsTarget.minus(idsSource)
    val idsUpdate = idsTarget.intersect(idsSource)
    val listDelete = target.filter {it.id in idsDelete}
    val listUpdate = source.filter {it.id in idsUpdate}
    val listInsert = source.filter {it.id in idsInsert}

    listDelete.forEach(::deleteTarget)
    listUpdate.forEach(::updateTarget)
    listInsert.forEach {
      insertTarget(it)
      listenerInsert(it)
    }
  }

  abstract fun deleteTarget(bean: T)
  abstract fun insertTarget(bean: T)
  abstract fun updateTarget(bean: T)
}

open class EntryID(val id: String) {
  override fun hashCode(): Int = id.hashCode()
  override fun equals(other: Any?): Boolean = id.equals(other)
}