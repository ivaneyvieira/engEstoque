package br.com.engecopi.estoque.model

import br.com.engecopi.estoque.model.RegistryUserInfo.abreviacaoDefault
import br.com.engecopi.estoque.model.finder.ViewCodBarConferenciaFinder
import br.com.engecopi.estoque.model.query.QViewCodBarCliente
import br.com.engecopi.estoque.model.query.QViewCodBarConferencia
import io.ebean.annotation.Cache
import io.ebean.annotation.View
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Id

@Cache(enableQueryCache = false)
@Entity
@View(name = "v_codigo_barra_conferencia")
class ViewCodBarConferencia {
  @Id
  @Column(name = "id_itens_nota")
  val id: Long = 0
  val codbar: String = ""
  val storeno: Int = 0
  val numero: String = ""
  val sequencia: Int = 0
  val abreviacao: String = ""
  
  companion object Find: ViewCodBarConferenciaFinder() {
    private fun findNotaCodbarCliente(key: String): ViewCodBarConferencia? {
      return QViewCodBarCliente().codbarLimpo.eq(key)
        .findList()
        .firstOrNull()
        ?.run {
          QViewCodBarConferencia().storeno.eq(storeno)
            .numero.eq(numero)
            .sequencia.eq(sequencia)
            .abreviacao.eq(abreviacaoDefault)
            .findList()
            .firstOrNull()
        }
    }
    
    private fun findNotaCodBarConferencia(key: String): ViewCodBarConferencia? {
      return QViewCodBarConferencia().codbar.eq(key)
        .findList()
        .firstOrNull()
    }
    
    fun findNota(key: String): ViewCodBarConferencia? {
      return findNotaCodbarCliente(key) ?: findNotaCodBarConferencia(key)
    }
    
    fun findKeyItemNota(key: String): List<ItemNota> {
      return QViewCodBarConferencia().codbar.eq(key)
        .findList()
        .mapNotNull {ItemNota.byId(it.id)}
    }
  }
}
