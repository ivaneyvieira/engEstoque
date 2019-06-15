package br.com.engecopi.estoque.ui.views

import br.com.engecopi.estoque.model.Loja
import br.com.engecopi.estoque.model.NotaSerie
import br.com.engecopi.estoque.model.RegistryUserInfo
import br.com.engecopi.estoque.viewmodel.UsuarioCrudVo
import br.com.engecopi.estoque.viewmodel.UsuarioViewModel
import br.com.engecopi.framework.ui.view.CrudLayoutView
import br.com.engecopi.framework.ui.view.CrudOperation.UPDATE
import br.com.engecopi.framework.ui.view.bindItensSet
import br.com.engecopi.framework.ui.view.expand
import br.com.engecopi.framework.ui.view.reloadBinderOnChange
import br.com.engecopi.framework.ui.view.row
import com.github.mvysny.karibudsl.v8.AutoView
import com.github.mvysny.karibudsl.v8.alignment
import com.github.mvysny.karibudsl.v8.bind
import com.github.mvysny.karibudsl.v8.checkBox
import com.github.mvysny.karibudsl.v8.checkBoxGroup
import com.github.mvysny.karibudsl.v8.comboBox
import com.github.mvysny.karibudsl.v8.expandRatio
import com.github.mvysny.karibudsl.v8.textField
import com.github.mvysny.karibudsl.v8.twinColSelect
import com.vaadin.ui.Alignment
import com.vaadin.ui.renderers.TextRenderer
import com.vaadin.ui.themes.ValoTheme

@AutoView
class UsuarioView: CrudLayoutView<UsuarioCrudVo, UsuarioViewModel>() {
  private val isAdmin = RegistryUserInfo.usuarioDefault.admin

  init {
    viewModel = UsuarioViewModel(this)
    layoutForm {
      formLayout.apply {
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
            setItemCaptionGenerator {it.sigla}
            bind(binder).bind(UsuarioCrudVo::loja)
            reloadBinderOnChange(binder)
          }

          checkBox("Administrador") {
            expandRatio = 1f
            bind(binder).bind(UsuarioCrudVo::admin)
            alignment = Alignment.BOTTOM_RIGHT
          }
          checkBox("Expedição") {
            expandRatio = 1f
            bind(binder).bind(UsuarioCrudVo::expedicao)
            alignment = Alignment.BOTTOM_RIGHT
          }
          checkBox("Estoque") {
            expandRatio = 1f
            bind(binder).bind(UsuarioCrudVo::estoque)
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
        row {
          // expand =1
          twinColSelect<String>("Localizações") {
            expandRatio = 1f
            //setWidth("95%")
            bindItensSet(binder, UsuarioCrudVo::locaisLoja.name)
            bind(binder).bind(UsuarioCrudVo::localizacaoes)
          }
        }
      }
      if(!isAdmin && operation == UPDATE) binder.setReadOnly(true)
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
        setRenderer({loja -> loja?.sigla ?: "Todas"}, TextRenderer())
      }
      column(UsuarioCrudVo::localStr) {
        expandRatio = 1
        caption = "Localização"
        setSortProperty("localizacaoesDefault")
      }
    }
  }
}
