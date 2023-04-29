package br.com.engecopi.framework.model

import java.io.IOException
import javax.servlet.*
import javax.servlet.annotation.WebFilter

@WebFilter(value = ["/*"])
class ConnectionFilter : Filter {
  @Throws(ServletException::class)
  override fun init(arg0: FilterConfig) { //Vazio
  }

  @Throws(IOException::class, ServletException::class)
  override fun doFilter(request: ServletRequest, response: ServletResponse, chain: FilterChain) {
    Transaction.execTransacao {
      chain.doFilter(request, response)
    }
  }

  override fun destroy() { //Vazio
  }
}
