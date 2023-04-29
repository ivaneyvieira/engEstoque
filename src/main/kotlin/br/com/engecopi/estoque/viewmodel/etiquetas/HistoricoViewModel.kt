package br.com.engecopi.estoque.viewmodel.etiquetas

import br.com.engecopi.estoque.model.HistoricoEtiqueta
import br.com.engecopi.estoque.model.Produto
import br.com.engecopi.estoque.model.Usuario
import br.com.engecopi.estoque.model.query.QHistoricoEtiqueta
import br.com.engecopi.framework.viewmodel.CrudViewModel
import br.com.engecopi.framework.viewmodel.EntityVo
import br.com.engecopi.framework.viewmodel.ICrudView
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

class HistoricoViewModel(view: IHistoricoView) :
  CrudViewModel<HistoricoEtiqueta, QHistoricoEtiqueta, HistoricoVo, IHistoricoView>(
    view
  ) {
  override fun update(bean: HistoricoVo) {
    bean.findEntity()?.let { hist ->
      hist.gtinOk = bean.gtinOk
      hist.save()
    }
  }

  override fun add(bean: HistoricoVo) { //Não implementado
  }

  override fun delete(bean: HistoricoVo) { //Não implementado
  }

  override fun newBean(): HistoricoVo {
    return HistoricoVo()
  }

  override val query: QHistoricoEtiqueta
    get() = QHistoricoEtiqueta().orderBy().id.desc()

  override fun HistoricoEtiqueta.toVO(): HistoricoVo {
    val bean = newBean()
    bean.id = this.id
    bean.gtinOk = this.gtinOk
    return bean
  }

  override fun QHistoricoEtiqueta.filterString(text: String): QHistoricoEtiqueta {
    return or().produto.codigo.contains(text).produto.grade.startsWith(text).endOr()
  }

  override fun QHistoricoEtiqueta.filterDate(date: LocalDate): QHistoricoEtiqueta {
    return data.eq(date)
  }
}

class HistoricoVo : EntityVo<HistoricoEtiqueta>() {
  override fun findEntity(): HistoricoEtiqueta? {
    return HistoricoEtiqueta.byId(id)
  }

  var id: Long = 0
  val usuario: Usuario?
    get() = findEntity()?.usuario
  val produto: Produto?
    get() = findEntity()?.produto
  val data: LocalDate?
    get() = findEntity()?.data
  val hora: LocalTime?
    get() = findEntity()?.hora
  val datahora
    get() = LocalDateTime.of(data, hora)
  val print: String?
    get() = findEntity()?.print
  val gtin: String?
    get() = findEntity()?.gtin
  var gtinOk: Boolean = false
}

interface IHistoricoView : ICrudView