package br.com.engecopi.estoque.ui

import br.com.engecopi.estoque.model.LoginInfo
import br.com.engecopi.estoque.model.Usuario
import br.com.engecopi.framework.ui.Session
import br.com.engecopi.saci.saci
import com.github.mvysny.karibudsl.v8.alignment
import com.github.mvysny.karibudsl.v8.button
import com.github.mvysny.karibudsl.v8.comboBox
import com.github.mvysny.karibudsl.v8.expandRatio
import com.github.mvysny.karibudsl.v8.fillParent
import com.github.mvysny.karibudsl.v8.horizontalLayout
import com.github.mvysny.karibudsl.v8.label
import com.github.mvysny.karibudsl.v8.onLeftClick
import com.github.mvysny.karibudsl.v8.panel
import com.github.mvysny.karibudsl.v8.passwordField
import com.github.mvysny.karibudsl.v8.px
import com.github.mvysny.karibudsl.v8.setPrimary
import com.github.mvysny.karibudsl.v8.textField
import com.github.mvysny.karibudsl.v8.verticalLayout
import com.github.mvysny.karibudsl.v8.w
import com.vaadin.icons.VaadinIcons
import com.vaadin.shared.ui.ContentMode.HTML
import com.vaadin.ui.Alignment
import com.vaadin.ui.ComboBox
import com.vaadin.ui.Notification
import com.vaadin.ui.TextField
import com.vaadin.ui.VerticalLayout
import com.vaadin.ui.themes.ValoTheme

object LoginService {
  fun login(loginInfo: LoginInfo) {
    EstoqueUI.current?.loginInfo = loginInfo
    Session[LoginInfo::class] = loginInfo
  }

  val currentUser: LoginInfo?
    get() = EstoqueUI.current?.loginInfo

  fun logout() {
    EstoqueUI.current?.loginInfo = null
  }
}

class LoginForm(private val appTitle: String): VerticalLayout() {
  private lateinit var username: TextField
  private lateinit var password: TextField
  private lateinit var abreviacao: ComboBox<String>

  init {
    setSizeFull()
    isResponsive = true
    panel {
      isResponsive = true
      w = 500.px
      alignment = Alignment.MIDDLE_CENTER
      verticalLayout {
        w = fillParent
        horizontalLayout {
          w = fillParent
          label("Login") {
            alignment = Alignment.BOTTOM_LEFT
            addStyleNames(ValoTheme.LABEL_H4, ValoTheme.LABEL_COLORED)
          }
          label(appTitle) {
            contentMode = HTML
            alignment = Alignment.BOTTOM_RIGHT
            styleName = ValoTheme.LABEL_H3
            expandRatio = 1f
          }
        }
        horizontalLayout {
          w = fillParent
          isResponsive = true
          username = textField("Usuário") {
            isResponsive = true
            expandRatio = 1f
            w = fillParent
            icon = VaadinIcons.USER
            styleName = ValoTheme.TEXTFIELD_INLINE_ICON
            addValueChangeListener {
              changeUserName(it.value)
            }
          }
          abreviacao = comboBox("Localizacao") {
            isResponsive = true
            isVisible = false
            expandRatio = 1f
            w = fillParent
            isEmptySelectionAllowed = false
            isTextInputAllowed = false
            val abreviacoes = abreviacaoes(username.value)
            this.setItems(abreviacoes)
            this.value = abreviacoes.firstOrNull()
          }
          password = passwordField("Senha") {
            isResponsive = true
            expandRatio = 1f
            w = fillParent
            icon = VaadinIcons.LOCK
            styleName = ValoTheme.TEXTFIELD_INLINE_ICON
          }
          button("Login") {
            isResponsive = true
            alignment = Alignment.BOTTOM_RIGHT
            setPrimary()
            onLeftClick {login()}
          }
        }
      }
    }
  }

  private fun changeUserName(loginName: String?) {
    if(::abreviacao.isInitialized) {
      val abreviacoes = abreviacaoes(loginName)

      abreviacao.setItems(abreviacoes)
      abreviacao.value = abreviacoes.firstOrNull()
      abreviacao.isVisible = abreviacoes.isNotEmpty()
    }
  }

  private fun abreviacaoes(username: String?): List<String> {
    return Usuario.abreviacaoes(username).sorted()
  }

  private fun login() {
    val user = saci.findUser(username.value)
    val pass = password.value
    val abrev = abreviacao.value ?: ""
    if(user == null || user.senha != pass) {
      Notification.show("Usuário ou senha inválidos. Por favor, tente novamente.")
      LoginService.logout()
    }
    else {
      val usuario = Usuario.findUsuario(user.login)
      if(usuario == null) {
        Notification.show("Usuário ou senha inválidos. Por favor, tente novamente.")
        LoginService.logout()
      }
      else {
        val loginInfo = LoginInfo(usuario, abrev)
        LoginService.login(loginInfo)
      }
    }
  }
}