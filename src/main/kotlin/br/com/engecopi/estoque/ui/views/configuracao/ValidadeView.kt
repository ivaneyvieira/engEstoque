package br.com.engecopi.estoque.ui.views.configuracao

import br.com.engecopi.estoque.model.RegistryUserInfo
import br.com.engecopi.estoque.viewmodel.configuracao.IValidadeView
import br.com.engecopi.estoque.viewmodel.configuracao.ValidadeViewModel
import br.com.engecopi.estoque.viewmodel.configuracao.ValidadeVo
import br.com.engecopi.framework.ui.view.CrudLayoutView
import br.com.engecopi.framework.ui.view.integerField
import br.com.engecopi.framework.ui.view.row
import com.github.mvysny.karibudsl.v8.*
import com.vaadin.ui.TextArea

@AutoView
class ValidadeView : CrudLayoutView<ValidadeVo, ValidadeViewModel>(false), IValidadeView {
    private lateinit var template: TextArea

    init {
        viewModel = ValidadeViewModel(this)
        layoutForm {
            formLayout.apply {
                w = 150.px
                h = 600.px
                row {
                    integerField("Validades") {
                        expandRatio = 4f
                        bind(binder).bind(ValidadeVo::mesesValidade)
                    }
                }
                row {
                    integerField("M. Fabricação") {
                        expandRatio = 4f
                        bind(binder).bind(ValidadeVo::mesesFabricacao)
                    }
                }
            }
        }
        form("Validade")
        gridCrud {
            deleteOperationVisible = RegistryUserInfo.usuarioDefault.admin
            column(ValidadeVo::mesesValidade) {
                expandRatio = 1
                caption = "Validade"
                setSortProperty("mesesValidade")
            }
            column(ValidadeVo::mesesFabricacao) {
                expandRatio = 1
                caption = "M. Fabricação"
                setSortProperty("mesesFabricacao")
            }
        }
    }
}