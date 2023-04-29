package br.com.engecopi.estoque.ui.views.movimentacao

/*
class VaadinValidadeValidator<V : NotaVo>(val binder: Binder<V>, val isAdmin: Boolean) : Validator<LocalDate> {
  override fun apply(value: LocalDate?,
                     context: ValueContext?): ValidationResult { //if (isAdmin) return ValidationResult.ok()
    value ?: return ValidationResult.ok()
    val bean = binder.bean ?: return ValidationResult.ok()
    val dataEntrada = bean.nota?.data
    val dataEmissao = bean.nota?.dataEmissao
    val mesesVencimento = bean.produto?.mesesVencimento ?: return ValidationResult.ok()
    val msgErro =
            ValidadeProduto.erroValidacao(
              produto = bean.produto?.codigo ?: "",
              dataValidade = value,
              dataEntrada = dataEntrada,
              dataEmissao = dataEmissao,
              mesesValidade = mesesVencimento,
                                         )
    msgErro ?: return ValidationResult.ok()
    return ValidationResult.error(msgErro)
  }
}
*/