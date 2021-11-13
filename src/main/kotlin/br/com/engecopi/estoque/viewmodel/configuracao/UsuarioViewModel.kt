package br.com.engecopi.estoque.viewmodel.configuracao

import br.com.engecopi.estoque.model.Loja
import br.com.engecopi.estoque.model.NotaSerie
import br.com.engecopi.estoque.model.Produto
import br.com.engecopi.estoque.model.Usuario
import br.com.engecopi.estoque.model.query.QProduto
import br.com.engecopi.estoque.model.query.QUsuario
import br.com.engecopi.framework.viewmodel.CrudViewModel
import br.com.engecopi.framework.viewmodel.EntityVo
import br.com.engecopi.framework.viewmodel.ICrudView

class UsuarioViewModel(view: IUsuarioView): CrudViewModel<Usuario, QUsuario, UsuarioCrudVo, IUsuarioView>(view) {
  override fun newBean(): UsuarioCrudVo {
    return UsuarioCrudVo()
  }
  
  private val queryProduto get() = QProduto()

  fun findProduto(offset: Int, limit: Int): List<Produto> {
    return queryProduto.setFirstRow(offset).setMaxRows(limit).findList()
  }

  fun countProduto(): Int {
    return queryProduto.findCount()
  }

  override fun update(bean: UsuarioCrudVo) {
    bean.entityVo?.let {usuario ->
      val loginName = bean.loginName ?: ""
      if(loginName.isNotBlank()) usuario.loginName = loginName
      usuario.loja = bean.loja
      usuario.locais = bean.localizacaoes.toList()
      usuario.estoque = bean.estoque
      usuario.series = bean.series.toList()
      usuario.expedicao = bean.expedicao
      usuario.painel = bean.painel
      usuario.configuracao = bean.configuracao
      usuario.entregaFutura = bean.entregaFutura
      usuario.ressuprimento = bean.ressuprimento
      usuario.abastecimento = bean.abastecimento
      usuario.admin = bean.admin ?: false
      usuario.etiqueta = bean.etiqueta
      usuario.impressora = bean.impressora ?: ""
      usuario.update()
    }
  }

  override fun add(bean: UsuarioCrudVo) {
    val usuario = Usuario().apply {
      this.loginName = bean.loginName ?: ""
      this.impressora = bean.impressora ?: ""
      this.loja = bean.loja
      this.locais = bean.localizacaoes.toList()
      this.series = bean.series.toList()
      this.estoque = bean.estoque
      this.expedicao = bean.expedicao
      this.painel = bean.painel
      this.configuracao = bean.configuracao
      this.entregaFutura = bean.entregaFutura
      this.ressuprimento = bean.ressuprimento
      this.abastecimento = bean.abastecimento
      this.admin = bean.admin ?: false
      this.etiqueta = bean.etiqueta
    }
    usuario.insert()
  }

  override val query: QUsuario
    get() = QUsuario().loginName.`in`(Usuario.findLoginUser())

  override fun Usuario.toVO(): UsuarioCrudVo {
    val usuario = this
    return UsuarioCrudVo().apply {
      entityVo = usuario
      this.loginName = usuario.loginName
      this.impressora = usuario.impressora
      this.loja = usuario.loja
      this.localizacaoes = usuario.locais.toHashSet()
      this.series = usuario.series.toSet()
      this.estoque = usuario.estoque
      this.expedicao = usuario.expedicao
      this.painel = usuario.painel
      this.configuracao = usuario.configuracao
      this.entregaFutura = usuario.entregaFutura
      this.ressuprimento = usuario.ressuprimento
      this.abastecimento = usuario.abastecimento
      this.admin = usuario.admin
      this.etiqueta = usuario.etiqueta
    }
  }

  override fun QUsuario.filterString(text: String): QUsuario {
    return loginName.contains(text)
  }

  override fun delete(bean: UsuarioCrudVo) {
    Usuario.findUsuario(bean.loginName ?: "")?.delete()
  }

  val lojas
    get() = Loja.all()
  val produtos: List<Produto>
    get() = Produto.all()
}

class UsuarioCrudVo: EntityVo<Usuario>() {
  override fun findEntity(): Usuario? {
    return Usuario.findUsuario(loginName)
  }

  var loginName: String? = ""
  var impressora: String? = ""
  var loja: Loja? = null
    set(value) {
      field = value
      locaisLoja.clear()
      val sets = value?.findAbreviacores().orEmpty().toMutableSet()
      locaisLoja.addAll(sets)
    }
  val nome
    get() = Usuario.nomeSaci(loginName ?: "")
  var locaisLoja: MutableSet<String> = HashSet()
  var series: Set<NotaSerie> = HashSet()
  var localizacaoes: Set<String> = HashSet()
  val localStr
    get() = localizacaoes.joinToString()
  var estoque: Boolean = true
  var expedicao: Boolean = false
  var painel: Boolean = false
  var configuracao: Boolean = false
  var entregaFutura: Boolean = false
  var ressuprimento: Boolean = false
  var abastecimento: Boolean = false
  var admin: Boolean? = false
  var etiqueta = false
  val tipoUsuarioStr
    get() = tiposUsuario().joinToString(separator = "/")

  private fun tiposUsuario(): List<String> {
    val tipos = mutableListOf<String>()
    if(admin == true) tipos.add("Administrado")
    else {
      if(estoque) tipos.add("Movimentação")
      if(expedicao) tipos.add("Expedicao")
      if(entregaFutura) tipos.add("Entrega Futura")
      if(painel) tipos.add("Painel")
      if(configuracao) tipos.add("Configuração")
      if(ressuprimento) tipos.add("Ressuprimento")
      if(abastecimento) tipos.add("Abastecimento")
      if(etiqueta) tipos.add("Etiqueta")
    }

    return tipos
  }
}

interface IUsuarioView: ICrudView
