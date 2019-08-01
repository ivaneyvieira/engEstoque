package br.com.engecopi.estoque.model

import br.com.engecopi.framework.model.Transaction
import com.vaadin.server.Page

object RegistryUserInfo {
  const val LOJA_FIELD = "LOJA_DEFAULT"
  private const val USER_FIELD = "USER_DEFAULT"
  private const val ABREV_FIELD = "ABREV_DEFAULT"
  private var loginInfo: () -> LoginInfo? = {null}

  fun register(loginInfoReg: () -> LoginInfo?) {
    this.loginInfo = loginInfoReg
  }

  private val info: LoginInfo
    get() {
      val info = loginInfo()
      if(info == null) {
        Transaction.variable(LOJA_FIELD, "NULL")
        Transaction.variable(USER_FIELD, "NULL")
        Transaction.variable(ABREV_FIELD, "NULL")
      }
      else {
        Transaction.variable(LOJA_FIELD, "${info.usuario.loja?.numero}")
        Transaction.variable(USER_FIELD, "${info.usuario.id}")
        Transaction.variable(ABREV_FIELD, "'${info.abreviacao}'")
      }
      return info!!
    }
  val usuarioDefault
    get() = info.usuario
  val abreviacaoDefault
    get() = info.abreviacao
  val lojaDefault
    get() = usuarioDefault.loja!!
  val userDefaultIsAdmin
    get() = usuarioDefault.admin
  val endereco
    get() = Page.getCurrent().webBrowser.address ?: ""
}

data class LoginInfo(val usuario: Usuario, val abreviacao: String)

enum class TipoUsuario(val descricao: String) {
  ESTOQUE("Estoque"),
  EXPEDICAO("Expedição")
}