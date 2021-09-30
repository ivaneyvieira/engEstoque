package br.com.engecopi.estoque.ui.views.movimentacao

import br.com.engecopi.estoque.viewmodel.movimentacao.NotaVo
import br.com.engecopi.utils.format
import com.vaadin.data.Binder
import com.vaadin.data.ValidationResult
import com.vaadin.data.Validator
import com.vaadin.data.ValueContext
import java.time.LocalDate

class ValidatorVencimento<V : NotaVo>(val binder: Binder<V>, val isAdmin: Boolean) : Validator<LocalDate> {
  override fun apply(value: LocalDate?, context: ValueContext?): ValidationResult {
   //if (isAdmin) return ValidationResult.ok()
    value ?: return ValidationResult.ok()
    val dataFabricacao = binder.bean.dataFabricacao(value) ?: return ValidationResult.ok()
    val dataMaxRecebimento = binder.bean.dataMaxRecebimento(value) ?: return ValidationResult.ok()
    return if (LocalDate.now()
              .isBefore(dataFabricacao.withDayOfMonth(1))
    ) ValidationResult.error("Mes de validade inválido (antes de ${dataFabricacao.format()})")
    else if (LocalDate.now()
              .isAfter(dataMaxRecebimento.withDayOfMonth(dataMaxRecebimento.lengthOfMonth()))
    ) ValidationResult.error("Mes de validade inválido (depois de ${dataMaxRecebimento.format()})")
    else ValidationResult.ok()
  }
}
