package br.com.engecopi.estoque.model

import br.com.engecopi.framework.model.Transaction
import br.com.engecopi.framework.viewmodel.EViewModelError
import com.vaadin.server.Page

object RegistryUserInfo {
  private const val LOJA_FIELD = "LOJA_DEFAULT"
  private const val USER_FIELD = "USER_DEFAULT"
  private const val ABREV_FIELD = "ABREV_DEFAULT"
  var loginInfoProvider: LoginInfoProvider? = null
  val loginInfo
    get() = loginInfoProvider?.loginInfo
  private val info: LoginInfo?
    get() {
      if(loginInfo == null) {
        Transaction.variable(LOJA_FIELD, "NULL")
        Transaction.variable(USER_FIELD, "NULL")
        Transaction.variable(ABREV_FIELD, "NULL")
      }
      else {
        Transaction.variable(LOJA_FIELD, "${lojaDeposito.numero}")
        Transaction.variable(USER_FIELD, "${loginInfo?.usuario?.id}")
        Transaction.variable(ABREV_FIELD, "'${loginInfo?.abreviacao}'")
      }
      return loginInfo
    }
  val usuarioDefault
    get() = info?.usuario ?: throw EUsuarioNaoInicializado()
  val abreviacaoDefault
    get() = info?.abreviacao ?: throw EUsuarioNaoInicializado()
  val lojaDeposito
    get() = Loja.findLoja(4) ?: throw EViewModelError("Loja depósito não cadastrada")
  val userDefaultIsAdmin
    get() = usuarioDefault.admin
  val endereco
    get() = Page.getCurrent().webBrowser.address ?: ""
  val impressora
    get() = Abreviacao.findByAbreviacao(abreviacaoDefault)?.impressora ?: ""
  val isLogged
    get() = loginInfo != null
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