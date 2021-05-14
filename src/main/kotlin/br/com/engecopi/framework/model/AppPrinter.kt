package br.com.engecopi.framework.model

import br.com.engecopi.estoque.model.RegistryUserInfo.enderecoBrowser

object AppPrinter: PrinterCups(host = "172.20.47.2",
                               port = 631,
                               userName = "root",
                               localHost = enderecoBrowser)