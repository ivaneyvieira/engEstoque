package br.com.engecopi.estoque.ui.views.configuracao

import br.com.engecopi.estoque.model.Loja
import br.com.engecopi.estoque.model.NotaSerie
import br.com.engecopi.estoque.model.RegistryUserInfo
import br.com.engecopi.estoque.viewmodel.configuracao.IUsuarioView
import br.com.engecopi.estoque.viewmodel.configuracao.UsuarioCrudVo
import br.com.engecopi.estoque.viewmodel.configuracao.UsuarioViewModel
import br.com.engecopi.framework.model.AppPrinter
import br.com.engecopi.framework.model.PrinterInfo
import br.com.engecopi.framework.ui.view.*
import br.com.engecopi.framework.ui.view.CrudOperation.UPDATE
import com.github.mvysny.karibudsl.v8.*
import com.vaadin.ui.Alignment
import com.vaadin.ui.renderers.TextRenderer
import com.vaadin.ui.themes.ValoTheme

@AutoView class UsuarioView : CrudLayoutView<UsuarioCrudVo, UsuarioViewModel>(false), IUsuarioView {
  private val isAdmin = RegistryUserInfo.usuarioDefault.admin

  init {
    viewModel = UsuarioViewModel(this)
    layoutForm {
      formLayout.apply {
        grupo("Usuário") {
          row {
            textField {
              expandRatio = 1f
              caption = "Login Saci"
              isReadOnly = isAdmin == false
              bind(binder).bind(UsuarioCrudVo::loginName)
              addValueChangeListener {
                binder.readBean(binder.bean)
              }
            }
            textField {
              expandRatio = 4f
              caption = "Nome"
              isReadOnly = true
              bind(binder).bind(UsuarioCrudVo::nome.name)
            }
          }
          row {
            comboBox<Loja> {
              expandRatio = 1f
              caption = "Loja"
              isEmptySelectionAllowed = true
              isTextInputAllowed = false
              this.emptySelectionCaption = "Todas"
              setItems(viewModel.lojas)
              setItemCaptionGenerator { it.sigla }
              bind(binder).bind(UsuarioCrudVo::loja)
              reloadBinderOnChange(binder)
            }

            comboBox<PrinterInfo>("Impressora") {
              expandRatio = 1f
              val itens = AppPrinter.printersInfo
              setItems(itens)
              setItemCaptionGenerator { it.description }

              isTextInputAllowed = false
              bind(binder).withConverter({ printerInfo ->
                                           printerInfo?.name ?: ""
                                         }, { name ->
                                           itens.find { it.name == name }
                                         }).bind(UsuarioCrudVo::impressora)
            }

            checkBox("Administrador") {
              bind(binder).bind(UsuarioCrudVo::admin)
              alignment = Alignment.BOTTOM_RIGHT
            }
            checkBox("Paineis") {
              bind(binder).bind(UsuarioCrudVo::painel)
              alignment = Alignment.BOTTOM_RIGHT
            }
            checkBox("Expedição") {
              bind(binder).bind(UsuarioCrudVo::expedicao)
              alignment = Alignment.BOTTOM_RIGHT
            }
            checkBox("Entrega Futura") {
              bind(binder).bind(UsuarioCrudVo::entregaFutura)
              alignment = Alignment.BOTTOM_RIGHT
            }
            checkBox("Ressuprimento") {
              bind(binder).bind(UsuarioCrudVo::ressuprimento)
              alignment = Alignment.BOTTOM_RIGHT
            }
            checkBox("Abastecimento") {
              bind(binder).bind(UsuarioCrudVo::abastecimento)
              alignment = Alignment.BOTTOM_RIGHT
            }
            checkBox("Movimentacao") {
              bind(binder).bind(UsuarioCrudVo::estoque)
              alignment = Alignment.BOTTOM_RIGHT
            }
            checkBox("Configuração") {
              bind(binder).bind(UsuarioCrudVo::configuracao)
              alignment = Alignment.BOTTOM_RIGHT
            }
            checkBox("Etiquetas") {
              bind(binder).bind(UsuarioCrudVo::etiqueta)
              alignment = Alignment.BOTTOM_RIGHT
            }
          }
          row {
            checkBoxGroup<NotaSerie> {
              caption = "Tipo de notas"
              expandRatio = 1f
              setItems(NotaSerie.values)
              setItemCaptionGenerator {
                it.descricao
              }
              addStyleName(ValoTheme.OPTIONGROUP_HORIZONTAL)
              bind(binder).bind(UsuarioCrudVo::series)
            }
          }
        }
        grupo("Localização", true) {
          row {
            isExpanded = true
            twinColSelect<String> {
              this.w = 80.perc
              this.rows = 12
              bindItensSet(binder, UsuarioCrudVo::locaisLoja.name)
              bind(binder).bind(UsuarioCrudVo::localizacaoes)
              alignment = Alignment.TOP_LEFT
            }
          }
        }
      }
      if (!isAdmin && operation == UPDATE) binder.setReadOnly(true)
    }
    form("Usuários")
    gridCrud {
      deleteOperationVisible = RegistryUserInfo.usuarioDefault.admin
      column(UsuarioCrudVo::loginName) {
        expandRatio = 1
        caption = "Usuário"
        setSortProperty("loginName")
      }
      column(UsuarioCrudVo::nome) {
        expandRatio = 5
        caption = "Nome"
      }
      column(UsuarioCrudVo::tipoUsuarioStr) {
        expandRatio = 1
        caption = "Tipo de Usuário"
      }
      column(UsuarioCrudVo::loja) {
        expandRatio = 1
        caption = "Loja"
        setRenderer({ loja -> loja?.sigla ?: "Todas" }, TextRenderer())
      }
      column(UsuarioCrudVo::localStr) {
        expandRatio = 1
        caption = "Localização"
        setSortProperty("localizacaoesDefault")
      }
    }
  }
}
