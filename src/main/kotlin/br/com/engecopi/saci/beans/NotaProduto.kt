package br.com.engecopi.saci.beans

import br.com.engecopi.estoque.model.TipoNota
import br.com.engecopi.utils.localDate
import java.time.LocalDate

class NotaProduto(val invno: Int,
                  val storeno: Int,
                  val numero: String,
                  val serie: String,
                  val date: Int,
                  val dtEmissao: Int,
                  val tipo: String,
                  val cancelado: String,
                  val prdno: String,
                  val grade: String,
                  val localizacao: String,
                  val data_cadastro: LocalDate
                 )