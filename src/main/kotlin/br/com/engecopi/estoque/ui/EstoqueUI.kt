package br.com.engecopi.estoque.ui

import br.com.engecopi.estoque.model.LoginInfo
import br.com.engecopi.estoque.model.RegistryUserInfo
import br.com.engecopi.estoque.model.RepositoryAvisoNotas
import br.com.engecopi.estoque.model.Usuario
import br.com.engecopi.estoque.model.etlSaci.ETLEntregaFutura
import br.com.engecopi.estoque.model.etlSaci.ETLPedidoNotaRessuprimento
import br.com.engecopi.estoque.model.etlSaci.ETLPedidos
import br.com.engecopi.estoque.model.etlSaci.ETLTransferenciaAutomatica
import br.com.engecopi.estoque.ui.views.configuracao.AbreciacaoView
import br.com.engecopi.estoque.ui.views.configuracao.EtiquetaView
import br.com.engecopi.estoque.ui.views.configuracao.ProdutoView
import br.com.engecopi.estoque.ui.views.configuracao.UsuarioView
import br.com.engecopi.estoque.ui.views.entregaFutura.EntregaFuturaEditorView
import br.com.engecopi.estoque.ui.views.entregaFutura.EntregaFuturaView
import br.com.engecopi.estoque.ui.views.entregaFutura.NFVendaFuturaView
import br.com.engecopi.estoque.ui.views.etiquetas.HistoricoView
import br.com.engecopi.estoque.ui.views.etiquetas.LabelNotaView
import br.com.engecopi.estoque.ui.views.etiquetas.LabelProdutoView
import br.com.engecopi.estoque.ui.views.expedicao.EntregaClienteEditorView
import br.com.engecopi.estoque.ui.views.expedicao.EntregaClienteView
import br.com.engecopi.estoque.ui.views.expedicao.NFExpedicaoView
import br.com.engecopi.estoque.ui.views.movimentacao.EntradaView
import br.com.engecopi.estoque.ui.views.movimentacao.SaidaView
import br.com.engecopi.estoque.ui.views.paineis.PainelGeralView
import br.com.engecopi.estoque.ui.views.paineis.PedidoTransferenciaView
import br.com.engecopi.estoque.ui.views.ressuprimento.EntregaRessuprimentoEditorView
import br.com.engecopi.estoque.ui.views.ressuprimento.EntregaRessuprimentoView
import br.com.engecopi.estoque.ui.views.ressuprimento.PedidoRessuprimentoView
import br.com.engecopi.framework.ui.view.toViewName
import br.com.engecopi.utils.SystemUtils
import com.github.mvysny.karibudsl.v8.MenuButton
import com.github.mvysny.karibudsl.v8.VaadinDsl
import com.github.mvysny.karibudsl.v8.ValoMenu
import com.github.mvysny.karibudsl.v8.autoViewProvider
import com.github.mvysny.karibudsl.v8.onLeftClick
import com.github.mvysny.karibudsl.v8.valoMenu
import com.vaadin.annotations.JavaScript
import com.vaadin.annotations.PreserveOnRefresh
import com.vaadin.annotations.Push
import com.vaadin.annotations.Theme
import com.vaadin.annotations.Title
import com.vaadin.annotations.VaadinServletConfiguration
import com.vaadin.annotations.Viewport
import com.vaadin.icons.VaadinIcons.BARCODE
import com.vaadin.icons.VaadinIcons.CART_O
import com.vaadin.icons.VaadinIcons.CLUSTER
import com.vaadin.icons.VaadinIcons.INBOX
import com.vaadin.icons.VaadinIcons.LINES_LIST
import com.vaadin.icons.VaadinIcons.NEWSPAPER
import com.vaadin.icons.VaadinIcons.OUT
import com.vaadin.icons.VaadinIcons.OUTBOX
import com.vaadin.icons.VaadinIcons.PACKAGE
import com.vaadin.icons.VaadinIcons.PAPERCLIP
import com.vaadin.icons.VaadinIcons.TRUCK
import com.vaadin.icons.VaadinIcons.USER
import com.vaadin.navigator.Navigator
import com.vaadin.navigator.PushStateNavigation
import com.vaadin.navigator.ViewDisplay
import com.vaadin.server.ErrorEvent
import com.vaadin.server.Page
import com.vaadin.server.VaadinRequest
import com.vaadin.server.VaadinService
import com.vaadin.server.VaadinServlet
import com.vaadin.shared.Position.TOP_CENTER
import com.vaadin.shared.communication.PushMode
import com.vaadin.ui.Notification
import com.vaadin.ui.Notification.Type.ERROR_MESSAGE
import com.vaadin.ui.UI
import com.vaadin.ui.themes.ValoTheme
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.slf4j.bridge.SLF4JBridgeHandler
import javax.servlet.ServletContextEvent
import javax.servlet.ServletContextListener
import javax.servlet.annotation.WebListener
import javax.servlet.annotation.WebServlet
import javax.servlet.http.Cookie

@Theme("mytheme")
@Title("Controle de estoque")
@Viewport("width=device-width, initial-scale=1.0")
@JavaScript("https://code.jquery.com/jquery-2.1.4.min.js", "https://code.responsivevoice.org/responsivevoice.js")
@PushStateNavigation
@PreserveOnRefresh
@Push(PushMode.MANUAL)
class EstoqueUI: UI() {
  private lateinit var menuVisaoGeral: MenuButton
  val title = "<h3>Estoque <strong>Engecopi</strong></h3>"
  private val versao = SystemUtils.readFile("/versao.txt")
  var loginInfo: LoginInfo? = null
  
  fun updateLogin() {
    updateContent("")
  }
  
  override fun init(request: VaadinRequest?) {
    isResponsive = true
    updateContent(request?.contextPath ?: "")
  }
  
  private fun updateBag(repo: RepositoryAvisoNotas) {
    access {
      if(loginInfo != null) {
        val qtWarnings = repo.qtWarning()
        if(qtWarnings == 0) {
          menuVisaoGeral.badge = ""
        }
        else {
          menuVisaoGeral.badge = "$qtWarnings"
        }
      }
    }
  }
  
  private fun updateContent(contextPath: String) {
    when(val info = loginInfo) {
      null -> loginScreen()
      else -> appScreen(info, contextPath)
    }
  }
  
  private fun appScreen(info: LoginInfo, contextPath: String) {
    content = null
    val user = info.usuario
    
    valoMenu {
      this.appTitle = title
      sectionLogin(user, info)
  
      if(user.rolePaineis()) sectionPaineis()
  
      if(user.roleExpedicao()) sectionExpedicao(user)
  
      if(user.roleFutura()) sectionFutura(user)
  
      if(user.roleRessuprimento()) sectionRessuprimento(user)
  
      if(user.roleMovimentacao()) sessionMovimentacao()
  
      if(user.roleConfiguracao()) sectionConfiguracao(user)
  
      if(user.roleEtiqueta()) sectionEtiqueta(user)
    }
    
    navigator = Navigator(this, content as ViewDisplay)
    navigator.addProvider(autoViewProvider)
    setErrorHandler {e -> errorHandler(e)}
    val contextPathDeafualt = when {
      user.roleExpedicao() && contextPath == "" -> NFExpedicaoView::class.java.toViewName()
      user.roleFutura() && contextPath == ""    -> NFVendaFuturaView::class.java.toViewName()
      else                                      -> contextPath
    }
    
    navigator.navigateTo(contextPathDeafualt)
  }
  
  private fun @VaadinDsl ValoMenu.sectionEtiqueta(user: Usuario) {
    section("Etiquetas") {
      menuButton("Imprimir Produto", BARCODE, view = LabelProdutoView::class.java)
      if(user.admin) {
        menuButton("Imprimir Nota", BARCODE, view = LabelNotaView::class.java)
        menuButton("Histórico", BARCODE, view = HistoricoView::class.java)
      }
    }
  }
  
  private fun @VaadinDsl ValoMenu.sectionConfiguracao(user: Usuario) {
    section("Configuração") {
      menuButton("Produtos", PACKAGE, view = ProdutoView::class.java)
      if(user.admin) {
        menuButton("Usuários", USER, view = UsuarioView::class.java)
        menuButton("Etiquetas", PAPERCLIP, view = EtiquetaView::class.java)
        menuButton("Localizações", CART_O, view = AbreciacaoView::class.java)
      }
    }
  }
  
  private fun @VaadinDsl ValoMenu.sessionMovimentacao() {
    section("Movimentação") {
      menuButton("Entrada", INBOX, view = EntradaView::class.java) {
        this.id = "menuEntrada"
      }
      menuButton("Saída", OUTBOX, view = SaidaView::class.java)
    }
  }
  
  private fun @VaadinDsl ValoMenu.sectionExpedicao(user: Usuario) {
    section("Expedição") {
      if(!user.estoque || user.admin) {
        menuButton("Nota Fiscal", NEWSPAPER, view = NFExpedicaoView::class.java)
      }
      menuButton("Entrega ao Cliente", TRUCK, view = EntregaClienteView::class.java)
      menuButton("Editor de Entrega", TRUCK, view = EntregaClienteEditorView::class.java)
    }
  }
  
  private fun @VaadinDsl ValoMenu.sectionFutura(user: Usuario) {
    section("Entrega Futura") {
      menuButton("Nota Fiscal", NEWSPAPER, view = NFVendaFuturaView::class.java)
      menuButton("Entrega ao Cliente", TRUCK, view = EntregaFuturaView::class.java)
      menuButton("Editor de Entrega", TRUCK, view = EntregaFuturaEditorView::class.java)
    }
  }
  
  private fun @VaadinDsl ValoMenu.sectionRessuprimento(user: Usuario) {
    section("Ressuprimento") {
      menuButton("Lançamento de Ressuprimento", NEWSPAPER, view = PedidoRessuprimentoView::class.java)
      menuButton("Entrega de Ressuprimento", TRUCK, view = EntregaRessuprimentoView::class.java)
      menuButton("Editor de Ressupmento", TRUCK, view = EntregaRessuprimentoEditorView::class.java)
    }
  }
  
  private fun @VaadinDsl ValoMenu.sectionPaineis() {
    section("Paineis") {
      menuVisaoGeral = menuButton("Visão geral", CLUSTER, view = PainelGeralView::class.java)
      RepositoryAvisoNotas.addEvent {repo ->
        updateBag(repo)
      }
      menuButton("Pedidos de Transferencia", LINES_LIST, view = PedidoTransferenciaView::class.java)
    }
  }
  
  private fun @VaadinDsl ValoMenu.sectionLogin(user: Usuario, info: LoginInfo) {
    section("Login") {
      menuButton("Usuário:", badge = user.loginName)
      if(user.estoque || user.admin) {
        menuButton("Localizacao:", badge = info.abreviacao)
      }
      info.usuario.loja?.sigla?.let {siglaLoja ->
        menuButton("Loja:", badge = siglaLoja)
      }
      menuButton(caption = "Endereco: ", badge = RegistryUserInfo.enderecoBrowser)
      menuButton("Sair", icon = OUT) {
        onLeftClick {
          LoginService.logout()
        }
      }
    }
  }
  
  private fun Usuario.rolePaineis() = this.admin || this.painel
  private fun Usuario.roleExpedicao() = this.admin || this.expedicao
  private fun Usuario.roleFutura() = this.admin || this.entregaFutura
  private fun Usuario.roleMovimentacao() = this.admin || this.estoque
  private fun Usuario.roleConfiguracao() = this.admin || this.configuracao
  private fun Usuario.roleEtiqueta() = this.admin || this.etiqueta
  private fun Usuario.roleRessuprimento() = this.admin || this.ressuprimento
  
  private fun loginScreen() {
    content = LoginForm("$title <p align=\"right\">$versao</p>")
    navigator = null
  }
  
  private fun errorHandler(e: ErrorEvent) {
    log?.error("Erro não identificado ${e.throwable.message}", e.throwable)
    // when the exception occurs, show a nice notification
    Notification("Oops",
                 "\n" + "Ocorreu um erro e lamentamos muito isso. Já está trabalhando na correção!",
                 ERROR_MESSAGE).apply {
      styleName += " " + ValoTheme.NOTIFICATION_CLOSABLE
      position = TOP_CENTER
      show(Page.getCurrent())
    }
  }
  
  companion object {
    val current
      get() = getCurrent() as? EstoqueUI
  }
}

@WebListener
class Bootstrap: ServletContextListener {
  override fun contextDestroyed(sce: ServletContextEvent?) {
    log?.info("Shutting down")
    log?.info("Destroying VaadinOnKotlin")
    log?.info("Shutdown complete")
  }
  
  override fun contextInitialized(sce: ServletContextEvent?) {
    log?.info("Starting up")
    val home = System.getenv("HOME")
    val fileName = System.getenv("EBEAN_PROPS") ?: "$home/ebean.properties"
    System.setProperty("ebean.props.file", fileName)
    println("##################### $fileName")
  }
}

@WebServlet(urlPatterns = ["/*"], name = "MyUIServlet", asyncSupported = true)
@VaadinServletConfiguration(ui = EstoqueUI::class, productionMode = false)
class MyUIServlet: VaadinServlet() {
  companion object {
    init {
      // Vaadin logs into java.util.logging. Redirect that, so that all logging goes through slf4j.
      SLF4JBridgeHandler.removeHandlersForRootLogger()
      SLF4JBridgeHandler.install()
      //ETLVendasCaixa.start()
      ETLPedidos.start()
      ETLEntregaFutura.start()
      ETLTransferenciaAutomatica.start()
      ETLPedidoNotaRessuprimento.start()
    }
  }
}

fun setCookie(nome: String, valor: String) {
  // Create a new cookie
  val myCookie = Cookie(nome, valor)
  // Make cookie expire in 2 minutes
  myCookie.maxAge = 60 * 60 * 24 * 5
  // Set the cookie path.
  myCookie.path =
    VaadinService.getCurrentRequest()
      .contextPath
  // Save cookie
  VaadinService.getCurrentResponse()
    .addCookie(myCookie)
}

fun getCokies(name: String): String? {
  val cookie =
    VaadinService.getCurrentRequest()
      .cookies.toList()
      .firstOrNull {it.name == name}
  cookie?.let {
    setCookie(it.name, it.value)
  }
  return cookie?.value
}

val Any.log: Logger?
  get() = LoggerFactory.getLogger(javaClass)

