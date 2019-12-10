package br.com.engecopi.estoque.viewmodel.etiquetas

import br.com.engecopi.estoque.model.Etiqueta
import br.com.engecopi.estoque.model.StatusNota
import br.com.engecopi.estoque.model.query.QEtiqueta
import br.com.engecopi.framework.viewmodel.CrudViewModel
import br.com.engecopi.framework.viewmodel.EViewModelError
import br.com.engecopi.framework.viewmodel.EntityVo
import br.com.engecopi.framework.viewmodel.ICrudView

class EtiquetaViewModel(view: IEtiquetaView): CrudViewModel<Etiqueta, QEtiqueta, EtiquetaVo, IEtiquetaView>(view) {
  override fun newBean(): EtiquetaVo {
    return EtiquetaVo()
  }

  override fun update(bean: EtiquetaVo) {
    bean.entityVo?.apply {
      this.titulo = bean.titulo ?: throw EViewModelError("A etiqueta está sem título")
      this.template = bean.template ?: throw EViewModelError("O template está vazio")
      this.statusNota = bean.statusNota ?: throw EViewModelError("O tipo está vazio")
      this.etiquetaDefault = bean.etiquetaDefault
      this.updateOutros()
      update()
    }
  }

  override fun add(bean: EtiquetaVo) {
    Etiqueta().apply {
      this.titulo = bean.titulo ?: throw EViewModelError("A etiqueta está sem título")
      this.template = bean.template ?: throw EViewModelError("O template está vazio")
      this.statusNota = bean.statusNota ?: throw EViewModelError("O tipo está vazio")
      this.etiquetaDefault = bean.etiquetaDefault
      this.updateOutros()
      insert()
    }
  }

  override fun delete(bean: EtiquetaVo) {
    bean.entityVo?.delete()
  }

  override val query: QEtiqueta
    get() = QEtiqueta()

  override fun Etiqueta.toVO(): EtiquetaVo {
    val etiqueta = this
    return EtiquetaVo().apply {
      this.entityVo = etiqueta
      this.titulo = etiqueta.titulo
      this.template = etiqueta.template
      this.statusNota = etiqueta.statusNota
      this.etiquetaDefault = etiqueta.etiquetaDefault
    }
  }

  override fun QEtiqueta.filterString(text: String): QEtiqueta {
    return titulo.contains(text)
  }
}

class EtiquetaVo: EntityVo<Etiqueta>() {
  override fun findEntity(): Etiqueta? {
    return Etiqueta.find(titulo, statusNota)
  }

  var titulo: String? = ""
  var template: String? = ""
  var statusNota: StatusNota? = null
  var etiquetaDefault: Boolean = false
}

interface IEtiquetaView: ICrudView