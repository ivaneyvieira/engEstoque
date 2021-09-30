package br.com.engecopi.estoque.ui.views.movimentacao

import br.com.engecopi.estoque.viewmodel.movimentacao.NotaVo
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
    val mesesVencimento = binder.bean.produto?.mesesVencimento ?: return ValidationResult.ok()

    val dataValidadeMinima = dataValidadeMinima(mesesVencimento)
    val dataValidadeMaxima = dataValidadeMaxima(mesesVencimento)
    return if (value.isBefore(dataValidadeMinima)) ValidationResult.error("Mes de validade inválido (antes de " + "${dataValidadeMinima.formatMesAno()})")
    else if (value.isAfter(dataValidadeMaxima)) ValidationResult.error("Mes de validade inválido (depois de " + "${dataValidadeMaxima.formatMesAno()})")
    else ValidationResult.ok()
  }

  companion object {
    fun dataValidadeMinima(mesesVencimento: Int): LocalDate {
      val mesesVencimentoMinimo = (mesesVencimento * 3.0 / 4.0).roundToInt()
      val data = LocalDate.now().plusMonths(mesesVencimentoMinimo.toLong())
      return data.withDayOfMonth(1)
    }

    fun dataValidadeMaxima(mesesVencimento: Int): LocalDate {
      val data = LocalDate.now().plusMonths(mesesVencimento.toLong())
      return data.withDayOfMonth(data.lengthOfMonth())
    }
  }
}
