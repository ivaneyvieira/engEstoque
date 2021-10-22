package br.com.engecopi.estoque.viewmodel.movimentacao

import br.com.engecopi.utils.formatMesAno
import java.time.LocalDate
import kotlin.math.roundToInt

class ValidadeProduto {
  companion object {
    fun erroValidacao(produto: String,
                      dataValidade: LocalDate?,
                      dataEntrada: LocalDate?,
                      dataEmissao: LocalDate?,
                      mesesValidade: Int?): String? {
      return when (mesesValidade) {
        null -> {
          when (dataValidade) {
            null -> null
            else -> "Produto '$produto' não deve possui data de validade"
          }
        }
        else -> {
          when (dataValidade) {
            null -> "Produto '$produto' deveria ter data de validade"
            else -> {
              val dataValidadeMinima = dataValidadeMinima(dataEntrada, mesesValidade)
              val dataValidadeMaxima = dataValidadeMaxima(dataEmissao, mesesValidade)
              when {
                dataValidade.isBefore(dataValidadeMinima) -> "Produto '$produto'. Mes de validade inválido (antes de " + "${dataValidadeMinima.formatMesAno()})"
                dataValidade.isAfter(dataValidadeMaxima)  -> "Produto '$produto'. Mes de validade inválido (depois de " + "${dataValidadeMaxima.formatMesAno()})"
                else                                      -> null
              }
            }
          }
        }
      }
    }

    private fun dataValidadeMinima(dataEntrada: LocalDate?, mesesValidade: Int): LocalDate? {
      dataEntrada ?: return null
      val mesesVencimentoMinimo = (mesesValidade * 3.0 / 4.0).roundToInt()
      val data = dataEntrada.plusMonths(mesesVencimentoMinimo.toLong())
      return data.withDayOfMonth(1)
    }

    private fun dataValidadeMaxima(dataEmissao: LocalDate?, mesesValidade: Int): LocalDate? {
      dataEmissao ?: return null
      val data = dataEmissao.plusMonths(mesesValidade.toLong())
      return data.withDayOfMonth(data.lengthOfMonth())
    }
  }
}
