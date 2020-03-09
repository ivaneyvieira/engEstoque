package br.com.engecopi.estoque.model

import br.com.engecopi.estoque.model.TipoMov.ENTRADA
import br.com.engecopi.estoque.model.finder.ViewPedidoRessuprimentoFinder
import br.com.engecopi.estoque.model.query.QItemNota
import br.com.engecopi.estoque.model.query.QViewPedidoRessuprimento
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
@View(name = "v_pedido_ressuprimento", dependentTables = ["notas", "itens_nota"])
class ViewPedidoRessuprimento: BaseModel() {
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
    get() = Nota.findNota(loja, numero, tipoMov)
              ?.notaBaixa() ?: emptyList()
  val chave
    get() = "${loja?.numero} ${numero.replace('/', ' ')} $sequencia"
  
  companion object Find: ViewPedidoRessuprimentoFinder() {
    fun findSaida(loja: Loja, numero: String?, abreviacao: String?): ViewPedidoRessuprimento? {
      numero ?: return null
      abreviacao ?: return null
      return QViewPedidoRessuprimento().numero.eq(numero)
        .loja.equalTo(loja)
        .abreviacao.eq(abreviacao)
        .findList()
        .firstOrNull()
    }
    
    fun findExpedicao(nota: Nota): ViewPedidoRessuprimento? {
      return QViewPedidoRessuprimento().numero.eq(nota.numero)
        .loja.equalTo(nota.loja)
        .findList()
        .firstOrNull()
    }
  }
  
  fun findItens(): List<ItemNota> {
    return QItemNota().nota.id.eq(nota?.id)
      .localizacao.startsWith(abreviacao)
      .findList()
  }
}
