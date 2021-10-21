package br.com.engecopi.estoque.viewmodel.movimentacao

import br.com.engecopi.utils.formatMesAno
import com.vaadin.data.Binder
import com.vaadin.data.ValidationResult
import com.vaadin.data.Validator
import com.vaadin.data.ValueContext
import java.time.LocalDate
import kotlin.math.roundToInt

class ValidatorVencimento<V : NotaVo>(val binder: Binder<V>, val isAdmin: Boolean) : Validator<LocalDate> {
  override fun apply(value: LocalDate?,
                     context: ValueContext?): ValidationResult { //if (isAdmin) return ValidationResult.ok()
    value ?: return ValidationResult.ok()
    val bean = binder.bean ?: return ValidationResult.ok()
    val dataEntrada = bean.nota?.data
    val dataEmissao = bean.nota?.dataEmissao
    val mesesVencimento = bean.produto?.mesesVencimento ?: return ValidationResult.ok()
    val msgErro =
            erroValidacao(dataVencimento = value,
                          dataEntrada = dataEntrada,
                          dataEmissao = dataEmissao,
                          mesesVencimento = mesesVencimento) ?: return ValidationResult.ok()
    return ValidationResult.error(msgErro)
  }

  companion object {
    fun erroValidacao(dataVencimento: LocalDate?,
                      dataEntrada: LocalDate?,
                      dataEmissao: LocalDate?,
                      mesesVencimento: Int?): String? {
      mesesVencimento ?: return null
      dataVencimento ?: return null
      val dataValidadeMinima = dataValidadeMinima(dataEntrada, mesesVencimento)
      val dataValidadeMaxima = dataValidadeMaxima(dataEmissao, mesesVencimento)
      return when {
        dataVencimento.isBefore(dataValidadeMinima) -> "Mes de validade inválido (antes de " + "${dataValidadeMinima.formatMesAno()})"
        dataVencimento.isAfter(dataValidadeMaxima)  -> "Mes de validade inválido (depois de " + "${dataValidadeMaxima.formatMesAno()})"
        else                                        -> null
      }
    }

    private fun dataValidadeMinima(dataEntrada: LocalDate?, mesesVencimento: Int): LocalDate? {
      dataEntrada ?: return null
      val mesesVencimentoMinimo = (mesesVencimento * 3.0 / 4.0).roundToInt()
      val data = dataEntrada.plusMonths(mesesVencimentoMinimo.toLong())
      return data.withDayOfMonth(1)
    }

    private fun dataValidadeMaxima(dataEmissao: LocalDate?, mesesVencimento: Int): LocalDate? {
      dataEmissao ?: return null
      val data = dataEmissao.plusMonths(mesesVencimento.toLong())
      return data.withDayOfMonth(data.lengthOfMonth())
    }
  }
}
