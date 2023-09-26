package br.com.engecopi.estoque.viewmodel.movimentacao

import br.com.engecopi.estoque.model.RegistryUserInfo.usuarioDefault
import br.com.engecopi.estoque.model.Validade
import br.com.engecopi.utils.formatMesAno
import java.time.LocalDate

class ValidadeProduto(private val msgErro: String?) {
  fun isOk() = msgErro == null
  fun isError() = msgErro != null
  fun msgErro() = msgErro ?: "Erro desconhecido"

  companion object {
    fun erroMesFabricacao(
      produto: String,
      dataFabricacao: LocalDate?,
      dataEntrada: LocalDate,
      mesesValidade: Int
    ): ValidadeProduto {
      if (usuarioDefault.admin) return ValidadeProduto(null)
      dataFabricacao ?: return ValidadeProduto(null)
      if(mesesValidade == 0) return ValidadeProduto(null)
      // val dataValidadeMaxima = dataEntrada?: return ValidadeProduto("A Nota não possui data de entrada")
      // val dataValidadeMinima = dataValidadeMaxima.minusMonths(round(mesesValidade * 3.00 / 4.00).toLong())

      Validade.updateList()
      val mesesFabricacao = Validade.findMesesFabricacao(mesesValidade)
                            ?: return ValidadeProduto("Configuração de meses de fabricação não encontrada")

      val dataValidadeMaxima = dataEntrada
      val dataValidadeMinima = dataValidadeMaxima.minusMonths((mesesFabricacao - 1).toLong()).withDayOfMonth(1)

      return when {
        dataFabricacao.isBefore(dataValidadeMinima) -> {
          ValidadeProduto(
            "Produto '$produto'. Fabricação a partir do mês ${
              dataValidadeMinima.formatMesAno()
            })"
          )
        }

        dataFabricacao.isAfter(dataValidadeMaxima)  -> {
          ValidadeProduto(
            "Produto '$produto'. Fabricação antes do mês ${
              dataValidadeMaxima.formatMesAno()
            })"
          )
        }

        else                                        -> ValidadeProduto(null)
      }
    }
  }
}

