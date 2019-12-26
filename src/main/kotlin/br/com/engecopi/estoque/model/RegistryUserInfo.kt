package br.com.engecopi.estoque.model

import br.com.engecopi.estoque.model.envelopes.Printer
import br.com.engecopi.framework.model.Transaction
import br.com.engecopi.framework.viewmodel.EViewModelError
import com.vaadin.server.Page

object RegistryUserInfo {
  private const val LOJA_FIELD = "LOJA_DEFAULT"
  private const val USER_FIELD = "USER_DEFAULT"
  private const val ABREV_FIELD = "ABREV_DEFAULT"
  var loginInfoProvider: LoginInfoProvider? = null
    set(value) {
      field = value
      val loginInfo = value?.loginInfo
      if(loginInfo == null) {
        Transaction.variable(LOJA_FIELD, "NULL")
        Transaction.variable(USER_FIELD, "NULL")
        Transaction.variable(ABREV_FIELD, "NULL")
      }
      else {
        Transaction.variable(LOJA_FIELD, "${lojaDeposito.numero}")
        Transaction.variable(USER_FIELD, "${loginInfo.usuario.id}")
        Transaction.variable(ABREV_FIELD, "'${loginInfo.abreviacao}'")
      }
    }
  private val info: LoginInfo?
    get() {
      val loginProv = loginInfoProvider ?: throw EViewModelError("Não há provedor de login")
      return loginProv.loginInfo
    }
  val usuarioDefault
    get() = info?.usuario ?: throw EUsuarioNaoInicializado()
  val abreviacaoDefault
    get() = info?.abreviacao ?: throw EUsuarioNaoInicializado()
  val lojaDeposito
    get() = Loja.findLoja(4) ?: throw EViewModelError("Loja depósito não cadastrada")
  val userDefaultIsAdmin
    get() = usuarioDefault.admin
  val enderecoBrowser
    get() = Page.getCurrent().webBrowser.address ?: ""
  val impressoraUsuario
    get() = Printer(Abreviacao.findByAbreviacao(abreviacaoDefault)?.impressora ?: "")
  val isLogged
    get() = info != null
}

data class LoginInfo(val usuario: Usuario, val abreviacao: String)

enum class TipoUsuario(val descricao: String) {
  ESTOQUE("Estoque"),
  EXPEDICAO("Expedição")
}

class EUsuarioNaoInicializado: EViewModelError("O usuário não está logado")

interface LoginInfoProvider {
  val loginInfo: LoginInfo?
}