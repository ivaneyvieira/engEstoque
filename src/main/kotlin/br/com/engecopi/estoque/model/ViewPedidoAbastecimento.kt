package br.com.engecopi.estoque.model

import br.com.engecopi.estoque.model.TipoMov.ENTRADA
import br.com.engecopi.estoque.model.finder.ViewPedidoAbastecimentoFinder
import br.com.engecopi.estoque.model.query.QViewPedidoAbastecimento
import br.com.engecopi.framework.model.BaseModel
import io.ebean.annotation.Cache
import io.ebean.annotation.View
import java.time.LocalDate
import java.time.LocalTime
import javax.persistence.CascadeType.MERGE
import javax.persistence.CascadeType.PERSIST
import javax.persistence.CascadeType.REFRESH
import javax.persistence.Entity
import javax.persistence.EnumType
import javax.persistence.Enumerated
import javax.persistence.ManyToOne

@Cache(enableQueryCache = false)
@Entity
@View(name = "v_pedido_abastecimento", dependentTables = ["notas", "itens_nota"])
class ViewPedidoAbastecimento: BaseModel() {
  @ManyToOne(cascade = [PERSIST, MERGE, REFRESH])
  var nota: Nota? = null
  var numero: String = ""
  @Enumerated(EnumType.STRING)
  var tipoMov: TipoMov = ENTRADA
  @Enumerated(EnumType.STRING)
  var tipoNota: TipoNota? = null
  var rota: String = ""
  var fornecedor: String = ""
  var cliente: String = ""
  var data: LocalDate = LocalDate.now()
  var dataEmissao: LocalDate = LocalDate.now()
  var lancamento: LocalDate = LocalDate.now()
  var hora: LocalTime = LocalTime.now()
  var observacao: String = ""
  @ManyToOne(cascade = [PERSIST, MERGE, REFRESH])
  var loja: Loja? = null
  var sequencia: Int = 0
  @ManyToOne(cascade = [PERSIST, MERGE, REFRESH])
  var usuario: Usuario? = null
  var abreviacao: String? = ""
  var codigoBarraConferencia: String? = ""
  val numeroBaixa
    get() = Nota.findNota(loja, numero, tipoMov)?.notaBaixa() ?: emptyList()
  
  companion object Find: ViewPedidoAbastecimentoFinder() {
    fun findSaida(loja: Loja, numero: String?, abreviacao: String?): ViewPedidoAbastecimento? {
      numero ?: return null
      abreviacao ?: return null
      return QViewPedidoAbastecimento().numero.eq(numero)
        .loja.equalTo(loja)
        .abreviacao.eq(abreviacao)
        .findList()
        .firstOrNull()
    }
    
    fun findExpedicao(nota: Nota): ViewPedidoAbastecimento? {
      return QViewPedidoAbastecimento().numero.eq(nota.numero)
        .loja.equalTo(nota.loja)
        .findList()
        .firstOrNull()
    }
  }
}
