package coffee.michel.ogma.logging

import org.springframework.core.annotation.Order
import org.springframework.stereotype.Component
import org.springframework.web.filter.CommonsRequestLoggingFilter
import javax.servlet.http.HttpServletRequest

@Component
@Order(3)
class RestServiceIncomingRequestLoggingFilter : CommonsRequestLoggingFilter() {
    init {
        this.isIncludePayload = true
        this.isIncludeQueryString = true
        this.maxPayloadLength = 10000
        this.isIncludeHeaders = false
        this.setAfterMessagePrefix("REQUEST DATA : ")
    }

    override fun shouldLog(request: HttpServletRequest): Boolean {
        return this.logger.isInfoEnabled
    }

    override fun afterRequest(request: HttpServletRequest, message: String) {
        this.logger.info(message)
    }

    override fun beforeRequest(request: HttpServletRequest, message: String) {
        this.logger.info(message)
    }
}
