package coffee.michel.ogma.logging

import mu.KotlinLogging
import org.springframework.core.annotation.Order
import org.springframework.stereotype.Component
import javax.servlet.Filter
import javax.servlet.FilterChain
import javax.servlet.ServletRequest
import javax.servlet.ServletResponse
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse


private val log = KotlinLogging.logger {}

@Component
@Order(2)
class RequestResponseLoggingFilter : Filter {

    override fun doFilter(request: ServletRequest?, response: ServletResponse?, chain: FilterChain?) {
        val req = request as HttpServletRequest
        val res = response as HttpServletResponse
        log.info { "Logging Request  ${req.method} : ${req.requestURI}" }
        chain!!.doFilter(request, response)
        log.info { "Logging Response Status :${res.status}" }
    }
}
