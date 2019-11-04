package br.com.engecopi.framework.viewmodel

import br.com.engecopi.framework.model.BaseModel
import br.com.engecopi.utils.parserDate
import io.ebean.PagedList
import io.ebean.typequery.TQRootBean
import java.time.LocalDate
import java.time.format.DateTimeFormatter

abstract class CrudViewModel<MODEL: BaseModel, Q: TQRootBean<MODEL, Q>, VO: EntityVo<MODEL>, V : ICrudView>(view: V):
  ViewModel<V>(view) {
  private var queryView: QueryView? = null
  private var pagedList: PagedList<MODEL>? = null
  var crudBean: VO? = null
  var resultadoOK = false

  protected abstract fun update(bean: VO)
  protected abstract fun add(bean: VO)
  protected abstract fun delete(bean: VO)
  protected abstract fun newBean(): VO

  fun read(bean: VO) {
    val entity = bean.toEntity() ?: throw EViewModel("Registro inválido")
    entity.refresh()
    crudBean = entity.toVO()
  }

  fun update() = exec {
    resultadoOK = false
    crudBean?.let {bean ->
      update(bean)
      resultadoOK = true
    }
    view.updateView()
  }

  fun add() = exec {
    resultadoOK = false
    crudBean?.let {bean ->
      add(bean)
      resultadoOK = true
    }
    view.updateView()
  }

  fun delete() = exec {
    resultadoOK = false
    crudBean?.let {bean ->
      delete(bean)
      resultadoOK = true
    }
    crudBean = null
  }

  fun cleanBean() = exec {
    crudBean = newBean()
  }

  fun read() = exec {
    resultadoOK = false
    crudBean?.let {bean ->
      read(bean)
      resultadoOK = true
    }
  }

  //Query Lazy
  abstract val query: Q

  abstract fun MODEL.toVO(): VO

  open fun Q.filterString(text: String): Q {
    return this
  }

  open fun Q.filterInt(int: Int): Q {
    return this
  }

  open fun Q.filterDate(date: LocalDate): Q {
    return this
  }

  private fun Q.filterBlank(filter: String?): Q {
    return if(filter.isNullOrBlank()) this
    else {
      val date = filter.parserDate()
      val int = filter.toIntOrNull()
      val q1 = or().filterString(filter)
      val q2 = date?.let {q1.filterDate(it)} ?: q1
      val q3 = int?.let {q2.filterInt(it)} ?: q2
      q3.endOr()
    }
  }

  open fun Q.orderQuery(): Q {
    return this
  }

  private fun parserDate(filter: String): LocalDate? {
    val frm = DateTimeFormatter.ofPattern("dd/MM/yyyy")
    return try {
      LocalDate.parse(filter, frm)
    } catch(e: Exception) {
      null
    }
  }

  private fun Q.makeSort(sorts: List<Sort>): Q {
    return if(sorts.isEmpty()) this.orderQuery()
    else {
      val orderByClause = sorts.joinToString {"${it.propertyName} ${if(it.descending) "DESC" else "ASC"}"}
      orderBy(orderByClause)
    }
  }

  open fun findQuery(): List<VO> {
    val list = pagedList?.list.orEmpty()
    return list.map {model ->
      val vo = model.toVO()
      vo.apply {
        entityVo = model
      }
    }
  }

  open fun countQuery(): Int {
    return pagedList?.totalCount ?: 0
  }

  fun updateQueryView(queryView: QueryView) {
    if(this.queryView != queryView) {
      this.queryView = queryView
      pagedList = query.filterBlank(queryView.filter)
        .makeSort(queryView.sorts)
        .setFirstRow(queryView.offset)
        .setMaxRows(queryView.limit)
        .findPagedList()
      pagedList?.loadCount()
    }
  }

  fun existsBean(bean: VO): Boolean {
    val entity = bean.toEntity() ?: return false
    return pagedList?.list?.any {it.id == entity.id} ?: false
  }
}

data class Sort(val propertyName: String, val descending: Boolean = false)

abstract class EntityVo<MODEL: BaseModel> {
  open var entityVo: MODEL? = null
  var readOnly: Boolean = false

  open fun toEntity(): MODEL? {
    return entityVo ?: findEntity()
  }

  abstract fun findEntity(): MODEL?
}

data class QueryView(val offset: Int, val limit: Int, val filter: String, val sorts: List<Sort>)

interface ICrudView: IView{
  fun updateView()
}