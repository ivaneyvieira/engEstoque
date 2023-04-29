package br.com.engecopi.estoque.viewmodel.movimentacao

import br.com.engecopi.estoque.model.RegistryUserInfo.usuarioDefault
import br.com.engecopi.utils.formatMesAno
import java.time.LocalDate
import kotlin.math.round

class ValidadeProduto(private val msgErro: String?) {
  fun isOk() = msgErro == null
  fun isError() = msgErro != null
  fun msgErro() = msgErro ?: "Erro desconhecido"

  companion object {
    fun erroMesFabricacao(
      produto: String, dataFabricacao: LocalDate?, dataEntrada: LocalDate?, mesesValidade: Int?
    ): ValidadeProduto {
      if (usuarioDefault.admin) return ValidadeProduto(null)
      mesesValidade ?: return ValidadeProduto(null)
      dataFabricacao ?: return ValidadeProduto("Produto '$produto' não deve possui data de validade")
      val dataValidadeMaxima = dataEntrada ?: return ValidadeProduto("A Nota não possui data de entrada")
      val dataValidadeMinima = dataValidadeMaxima.minusMonths(round(mesesValidade * 3.00 / 4.00).toLong())

      return when {
        dataFabricacao.isBefore(dataValidadeMinima) -> {
          ValidadeProduto(
            "Produto '$produto'. Mes de fabricação inválido (antes de ${
              dataValidadeMinima.formatMesAno()
            })"
          )
        }

        dataFabricacao.isAfter(dataValidadeMaxima) -> {
          ValidadeProduto(
            "Produto '$produto'. Mes de fabricação inválido (depois de ${
              dataValidadeMaxima.formatMesAno()
            })"
          )
        }

        else -> ValidadeProduto(null)
      }
    }
  }
}

