package br.com.engecopi.estoque.viewmodel.configuracao

import br.com.engecopi.estoque.model.Validade
import br.com.engecopi.estoque.model.query.QValidade
import br.com.engecopi.framework.viewmodel.CrudViewModel
import br.com.engecopi.framework.viewmodel.EViewModelError
import br.com.engecopi.framework.viewmodel.EntityVo
import br.com.engecopi.framework.viewmodel.ICrudView

class ValidadeViewModel(view: IValidadeView): CrudViewModel<Validade, QValidade, ValidadeVo, IValidadeView>(view) {
  override fun newBean(): ValidadeVo {
    return ValidadeVo()
  }

  override fun update(bean: ValidadeVo) {
    bean.entityVo?.apply {
      this.mesesValidade = bean.mesesValidade ?: throw EViewModelError("Meses de validade não informado")
      this.mesesFabricacao = bean.mesesFabricacao ?: throw EViewModelError("Meses de fabricação não informado")
      update()
    }
  }

  override fun add(bean: ValidadeVo) {
    Validade().apply {
      this.mesesValidade = bean.mesesValidade ?: throw EViewModelError("Meses de validade não informado")
      this.mesesFabricacao = bean.mesesFabricacao ?: throw EViewModelError("Meses de fabricação não informado")
      insert()
    }
  }

  override fun delete(bean: ValidadeVo) {
    bean.entityVo?.delete()
  }

  override val query: QValidade
    get() = QValidade()

  override fun Validade.toVO(): ValidadeVo {
    val validade = this
    return ValidadeVo().apply {
      this.entityVo = validade
      this.mesesValidade = validade.mesesValidade
      this.mesesFabricacao = validade.mesesFabricacao
    }
  }

  override fun QValidade.filterString(text: String): QValidade {
    return QValidade().mesesValidade.eq(text.toIntOrNull())
  }
}

class ValidadeVo: EntityVo<Validade>() {
  override fun findEntity(): Validade? {
    return Validade.find(mesesValidade)
  }

  var mesesValidade: Int? = 0
  var mesesFabricacao: Int? = 0
}

interface IValidadeView: ICrudView