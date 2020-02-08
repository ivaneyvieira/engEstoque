package br.com.engecopi.estoque.model.etlSaci

import br.com.engecopi.estoque.model.RegistryUserInfo.isLogged
import br.com.engecopi.utils.parameterNames
import br.com.engecopi.utils.readInstanceProperty
import io.ebean.DB
import io.ebean.SqlUpdate

typealias ListenerEventUpdate<T> = (source: T, target: T) -> Unit
typealias ListenerEventInsert<T> = (source: T) -> Unit
typealias ListenerEventDelete<T> = (source: T) -> Unit
typealias  ListenerEvent<T> = (sources: List<T>) -> Unit

abstract class ETL<T: EntryID> {
  protected abstract val sqlDelete: String
  protected abstract val sqlInsert: String
  protected abstract val sqlUpdate: String
  private val listenerInsert = mutableMapOf<String, ListenerEventInsert<T>>()
  private val listenerDelete = mutableMapOf<String, ListenerEventDelete<T>>()
  private val listenerUpdate = mutableMapOf<String, ListenerEventUpdate<T>>()
  private val listener = mutableMapOf<String, ListenerEvent<T>>()
  
  fun addListenerInsert(name: String, listener: ListenerEventInsert<T>) {
    if(!listenerInsert.keys.contains(name)) this.listenerInsert[name] = listener
  }
  
  fun addListenerDelete(name: String, listener: ListenerEventDelete<T>) {
    if(!listenerDelete.keys.contains(name)) this.listenerDelete[name] = listener
  }
  
  fun addListenerUpdate(name: String, listener: ListenerEventUpdate<T>) {
    if(!listenerUpdate.keys.contains(name)) this.listenerUpdate[name] = listener
  }
  
  fun addListener(name: String, listener: ListenerEvent<T>) {
    if(!this.listener.keys.contains(name)) this.listener[name] = listener
  }
  
  fun execute(source: List<T>, target: List<T>): Int {
    val idsTarget = target.map {it.id}
    val idsSource = source.map {it.id}
    val idsInsert = idsSource.minus(idsTarget)
    val idsDelete = idsTarget.minus(idsSource)
    val listUpdate = target.mapNotNull {targetBean ->
      source.firstOrNull {sourceBean -> sourceBean.id == targetBean.id}
        ?.let {sourceBean ->
          if(sourceBean.chave == targetBean.chave) null
          else sourceBean
        }
    }
    val listDelete = target.filter {it.id in idsDelete}
    val listInsert = source.filter {it.id in idsInsert}
  
    deleteTarget(listDelete).forEach {bean ->
      listenerDelete.values.forEach {exec ->
        exec(bean)
      }
    }
    updateTarget(listUpdate).forEach {bean ->
      target.firstOrNull {it.id == bean.id}
        ?.let {targetBean ->
          listenerUpdate.values.forEach {exec ->
            exec(bean, targetBean)
          }
        }
    }
    insertTarget(listInsert).forEach {bean ->
      listenerInsert.values.forEach {exec ->
        exec(bean)
      }
    }
    val list = (listInsert + listDelete + listUpdate).distinct()
    if(list.isNotEmpty()) listener.values.forEach {exec ->
      exec(list)
    }
    return list.size
  }
  
  private fun deleteTarget(listBean: List<T>): List<T> {
    return execUpdate(sqlDelete, listBean)
  }
  
  private fun insertTarget(listBean: List<T>): List<T> {
    return execUpdate(sqlInsert, listBean)
  }
  
  private fun updateTarget(listBean: List<T>): List<T> {
    return execUpdate(sqlUpdate, listBean)
  }
  
  private fun execUpdate(sql: String, listBean: List<T>): List<T> {
    val sqlUpdate = DB.sqlUpdate(sql)
    DB.beginTransaction()
      .use {txn ->
        listBean.forEach {bean ->
          sqlUpdate
            .setParameter(bean)
            .addBatch()
        }
        txn.commit()
      }
    return listBean
  }
}

abstract class EntryID(val id: String) {
  override fun hashCode(): Int = id.hashCode()
  override fun equals(other: Any?): Boolean = id.equals(other)
  
  abstract val chave: String
}

fun <T: Any> SqlUpdate.setParameter(bean: T): SqlUpdate {
  val parameterNames = parameterNames(sql)
  parameterNames.forEach {param ->
    val value = readInstanceProperty(bean, param)
    setParameter(param, value)
  }
  return this
}

abstract class ETLThread<T: EntryID>(private val etl: ETL<T>, private val intervalInSeconds: Int) {
  val MAX_INTERVAL_SECONDS: Long = 60
  
  protected abstract fun getSource(): List<T>
  
  protected abstract fun getTarget(): List<T>
  
  val listDados
    get() = if(isLogged) getTarget() else emptyList()
  
  fun addListenerInsert(name: String, listener: ListenerEventInsert<T>) {
    etl.addListenerInsert(name, listener)
  }
  
  fun addListenerDelete(name: String, listener: ListenerEventDelete<T>) {
    etl.addListenerDelete(name, listener)
  }
  
  fun addListenerUpdate(name: String, listener: ListenerEventUpdate<T>) {
    etl.addListenerUpdate(name, listener)
  }
  
  fun addListener(name: String, listener: ListenerEvent<T>) {
    etl.addListener(name, listener)
  }
  
  fun update(): Int {
    return etl.execute(getSource(), getTarget())
  }
  
  private val thread = Thread {
    var interval = MAX_INTERVAL_SECONDS
    while(true) {
      try {
        val rows = update()
        interval = if(rows > 0) {
          interval / 2
        }
        else {
          interval * 2
        }.limitInterval()
      } catch(e: Throwable) {
        e.printStackTrace()
      }
      Thread.sleep((interval * 1000).toLong())
    }
  }
  
  private fun Long.limitInterval(): Long {
    return when {
      this > MAX_INTERVAL_SECONDS -> MAX_INTERVAL_SECONDS
      this <= 0                   -> 1
      else                        -> this
    }
  }
  
  fun start() {
    thread.start()
  }
  
  fun stop() {
    thread.interrupt()
  }
}