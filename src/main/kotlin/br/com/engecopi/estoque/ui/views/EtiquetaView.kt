package br.com.engecopi.estoque.ui.views

import br.com.engecopi.estoque.model.RegistryUserInfo
import br.com.engecopi.estoque.model.StatusNota
import br.com.engecopi.estoque.viewmodel.EtiquetaViewModel
import br.com.engecopi.estoque.viewmodel.EtiquetaVo
import br.com.engecopi.framework.ui.view.CrudLayoutView
import br.com.engecopi.framework.ui.view.default
import br.com.engecopi.framework.ui.view.row
import br.com.engecopi.utils.SystemUtils
import com.github.mvysny.karibudsl.v8.AutoView
import com.github.mvysny.karibudsl.v8.alignment
import com.github.mvysny.karibudsl.v8.bind
import com.github.mvysny.karibudsl.v8.button
import com.github.mvysny.karibudsl.v8.checkBox
import com.github.mvysny.karibudsl.v8.comboBox
import com.github.mvysny.karibudsl.v8.expandRatio
import com.github.mvysny.karibudsl.v8.h
import com.github.mvysny.karibudsl.v8.px
import com.github.mvysny.karibudsl.v8.textArea
import com.github.mvysny.karibudsl.v8.textField
import com.github.mvysny.karibudsl.v8.w
import com.vaadin.icons.VaadinIcons
import com.vaadin.ui.Alignment
import com.vaadin.ui.renderers.TextRenderer

@AutoView
class EtiquetaView: CrudLayoutView<EtiquetaVo, EtiquetaViewModel>() {
  init {
    viewModel = EtiquetaViewModel(this)
    layoutForm {
      formLayout.apply {
        w = 600.px
        h = 600.px
        row {
          textField("Título") {
            expandRatio = 4f
            bind(binder).bind(EtiquetaVo::titulo)
          }
        }
        row {
          comboBox<StatusNota>("Tipo") {
            expandRatio = 2f
            default {it.descricao}
            setItems(StatusNota.values().toList())
            bind(binder).bind(EtiquetaVo::statusNota)
          }
          checkBox("Etiqueta padrão") {
            expandRatio = 2f
            alignment = Alignment.BOTTOM_LEFT
            bind(binder).bind(EtiquetaVo::etiquetaDefault)
          }
          button("Ajuda") {
            alignment = Alignment.BOTTOM_RIGHT
            expandRatio = 1f
            icon = VaadinIcons.BOOK
            addClickListener {
              showInfo(SystemUtils.readFile("/html/variaveis.html") ?: "")
            }
          }
        }
        row {
          textArea("Template") {
            h = 400.px
            expandRatio = 1f
            bind(binder).bind(EtiquetaVo::template)
          }
        }
      }
    }
    form("Etiquetas")
    gridCrud {
      deleteOperationVisible = RegistryUserInfo.usuarioDefault.admin
      column(EtiquetaVo::titulo) {
        expandRatio = 1
        caption = "Título"
        setSortProperty("titulo")
      }
      column(EtiquetaVo::statusNota) {
        setRenderer({it?.descricao ?: ""}, TextRenderer())
        caption = "Tipo"
        setSortProperty("statusNota")
      }
      column(EtiquetaVo::etiquetaDefault) {
        caption = "Padrão"

        setRenderer({
                      when {
                        it == null -> ""
                        it         -> "Sim"
                        else       -> "Não"
                      }
                    }, TextRenderer())
      }
    }
  }
}