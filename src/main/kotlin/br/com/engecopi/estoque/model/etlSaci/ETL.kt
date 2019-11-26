package br.com.engecopi.estoque.model.etlSaci

import br.com.astrosoft.utils.parameterNames
import br.com.astrosoft.utils.readInstanceProperty
import br.com.engecopi.estoque.model.RegistryUserInfo.isLogged
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
  
  fun execute(source: List<T>, target: List<T>) {
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
    
    listDelete.forEach {bean ->
      deleteTarget(bean)
      listenerDelete.values.forEach {exec ->
        exec(bean)
      }
    }
    listUpdate.forEach {bean ->
      updateTarget(bean)
      val targetBean = target.firstOrNull {it.id == bean.id}
      if(targetBean != null) listenerUpdate.values.forEach {exec ->
        exec(bean, targetBean)
      }
    }
    listInsert.forEach {bean ->
      insertTarget(bean)
      listenerInsert.values.forEach {exec ->
        exec(bean)
      }
    }
    val list = (listInsert + listDelete + listUpdate).distinct()
    if(list.isNotEmpty()) listener.values.forEach {exec ->
      exec(list)
    }
  }
  
  private fun deleteTarget(bean: T) {
    execUpdate(sqlDelete, bean)
  }
  
  private fun insertTarget(bean: T) {
    execUpdate(sqlInsert, bean)
  }
  
  private fun updateTarget(bean: T) {
    execUpdate(sqlUpdate, bean)
  }
  
  private fun execUpdate(sql: String, bean: T) {
    DB.sqlUpdate(sql)
      .setParameter(bean)
      .execute()
  }
}

abstract class EntryID(val id: String) {
  override fun hashCode(): Int = id.hashCode()
  override fun equals(other: Any?): Boolean = id.equals(other)
  
  abstract val chave: String
}

fun <T: Any> SqlUpdate.setParameter(bean: T): SqlUpdate {
  parameterNames(sql).forEach {param ->
    val value: Any? = readInstanceProperty(bean, param)
    setParameter(param, value)
  }
  return this
}

abstract class ETLThread<T: EntryID>(private val etl: ETL<T>) {
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
  
  fun update() {
    etl.execute(getSource(), getTarget())
  }
  
  private val thread = Thread {
    while(true) {
      try {
        update()
      } catch(e: Throwable) {
        e.printStackTrace()
      }
      Thread.sleep(30000)
    }
  }
  
  fun start() {
    thread.start()
  }
  
  fun stop() {
    thread.interrupt()
  }
}