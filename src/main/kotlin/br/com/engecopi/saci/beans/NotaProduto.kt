package br.com.engecopi.saci.beans

import br.com.engecopi.estoque.model.TipoNota
import br.com.engecopi.utils.localDate

class NotaProduto(val invno: Int,
                  val storeno: Int,
                  val numero: String,
                  val serie: String,
                  val date: Int,
                  val dtEmissao: Int,
                  val tipo: String,
                  val cancelado: Int,
                  val prdno: String,
                  val grade: String
                 )