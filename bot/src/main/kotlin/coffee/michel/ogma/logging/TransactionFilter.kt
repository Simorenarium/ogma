package coffee.michel.ogma.logging

import mu.KotlinLogging
import org.springframework.core.annotation.Order
import org.springframework.stereotype.Component
import javax.servlet.Filter
import javax.servlet.FilterChain
import javax.servlet.ServletRequest
import javax.servlet.ServletResponse
import javax.servlet.http.HttpServletRequest


private val log = KotlinLogging.logger {}

@Component
@Order(1)
class TransactionFilter : Filter {
    override fun doFilter(request: ServletRequest?, response: ServletResponse?, chain: FilterChain?) {
        val req = request as HttpServletRequest
        log.debug{"Starting a transaction for req : ${req.requestURI}"}

        chain!!.doFilter(request, response)
        log.debug { "Committing a transaction for req : ${req.requestURI}"}
    }
}
