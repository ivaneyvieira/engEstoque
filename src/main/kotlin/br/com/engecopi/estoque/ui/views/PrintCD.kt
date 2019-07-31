package br.com.engecopi.estoque.ui.views

import br.com.engecopi.estoque.model.RegistryUserInfo
import br.com.engecopi.framework.ui.view.imprimeText

fun imprimeText(text: String?) {
  val printerName = RegistryUserInfo.abreviacaoDefault
  imprimeText(printerName, text)
}