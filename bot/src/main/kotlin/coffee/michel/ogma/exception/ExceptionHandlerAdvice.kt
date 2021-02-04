package coffee.michel.ogma.exception

import mu.KotlinLogging
import org.springframework.beans.ConversionNotSupportedException
import org.springframework.beans.TypeMismatchException
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.HttpStatus.CONFLICT
import org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR
import org.springframework.http.HttpStatus.NOT_FOUND
import org.springframework.http.ResponseEntity
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.http.converter.HttpMessageNotWritableException
import org.springframework.validation.BindException
import org.springframework.web.HttpMediaTypeNotAcceptableException
import org.springframework.web.HttpMediaTypeNotSupportedException
import org.springframework.web.HttpRequestMethodNotSupportedException
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.MissingPathVariableException
import org.springframework.web.bind.MissingServletRequestParameterException
import org.springframework.web.bind.ServletRequestBindingException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.context.request.WebRequest
import org.springframework.web.context.request.async.AsyncRequestTimeoutException
import org.springframework.web.multipart.support.MissingServletRequestPartException
import org.springframework.web.servlet.NoHandlerFoundException
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler

private val log = KotlinLogging.logger {}

@RestControllerAdvice
internal class ExceptionHandlerAdvice : ResponseEntityExceptionHandler() {

    @ExceptionHandler(Exception::class)
    fun handleAmbiguousException(ex: Exception, request: WebRequest): ResponseEntity<Any> {
        log.error(ex) { "An unknown error occurred: " }
        return assembleExceptionResponse(INTERNAL_SERVER_ERROR, ex)
    }

    override fun handleHttpMediaTypeNotSupported(
        ex: HttpMediaTypeNotSupportedException,
        headers: HttpHeaders,
        status: HttpStatus,
        request: WebRequest
    ) = assembleExceptionResponse(status, ex)

    override fun handleHttpMediaTypeNotAcceptable(
        ex: HttpMediaTypeNotAcceptableException,
        headers: HttpHeaders,
        status: HttpStatus,
        request: WebRequest
    ) = assembleExceptionResponse(status, ex)

    override fun handleHttpRequestMethodNotSupported(
        ex: HttpRequestMethodNotSupportedException,
        headers: HttpHeaders,
        status: HttpStatus,
        request: WebRequest
    ) = assembleExceptionResponse(status, ex)

    override fun handleMissingPathVariable(
        ex: MissingPathVariableException,
        headers: HttpHeaders,
        status: HttpStatus,
        request: WebRequest
    ) = assembleExceptionResponse(status, ex)

    override fun handleServletRequestBindingException(
        ex: ServletRequestBindingException,
        headers: HttpHeaders,
        status: HttpStatus,
        request: WebRequest
    ) = assembleExceptionResponse(status, ex)

    override fun handleConversionNotSupported(
        ex: ConversionNotSupportedException,
        headers: HttpHeaders,
        status: HttpStatus,
        request: WebRequest
    ) = assembleExceptionResponse(status, ex)

    override fun handleTypeMismatch(
        ex: TypeMismatchException,
        headers: HttpHeaders,
        status: HttpStatus,
        request: WebRequest
    ) = assembleExceptionResponse(status, ex)

    override fun handleHttpMessageNotWritable(
        ex: HttpMessageNotWritableException,
        headers: HttpHeaders,
        status: HttpStatus,
        request: WebRequest
    ) = assembleExceptionResponse(status, ex)

    override fun handleMissingServletRequestPart(
        ex: MissingServletRequestPartException,
        headers: HttpHeaders,
        status: HttpStatus,
        request: WebRequest
    ) = assembleExceptionResponse(status, ex)

    override fun handleBindException(ex: BindException, headers: HttpHeaders, status: HttpStatus, request: WebRequest) =
        assembleExceptionResponse(status, ex)

    override fun handleNoHandlerFoundException(
        ex: NoHandlerFoundException,
        headers: HttpHeaders,
        status: HttpStatus,
        request: WebRequest
    ) = assembleExceptionResponse(status, ex)

    override fun handleAsyncRequestTimeoutException(
        ex: AsyncRequestTimeoutException,
        headers: HttpHeaders,
        status: HttpStatus,
        webRequest: WebRequest
    ) = assembleExceptionResponse(status, ex)

    override fun handleExceptionInternal(
        ex: java.lang.Exception,
        body: Any?,
        headers: HttpHeaders,
        status: HttpStatus,
        request: WebRequest
    ) = assembleExceptionResponse(status, ex)

    override fun handleMissingServletRequestParameter(
        ex: MissingServletRequestParameterException,
        headers: HttpHeaders,
        status: HttpStatus,
        request: WebRequest
    ) = assembleExceptionResponse(status, ex)

    override fun handleHttpMessageNotReadable(
        ex: HttpMessageNotReadableException,
        headers: HttpHeaders,
        status: HttpStatus,
        request: WebRequest
    ) = assembleExceptionResponse(status, ex)

    private fun assembleExceptionResponse(httpStatus: HttpStatus, ex: Exception): ResponseEntity<Any> =
        ResponseEntity.status(httpStatus).body(assembleApiException(httpStatus, ex))

    private fun assembleApiException(httpStatus: HttpStatus, ex: Exception) =
        ApiException(httpStatus, ex.localizedMessage, mapOf())

    override fun handleMethodArgumentNotValid(
        ex: MethodArgumentNotValidException,
        headers: HttpHeaders,
        status: HttpStatus,
        request: WebRequest
    ): ResponseEntity<Any> =
        assembleExceptionResponse(status, headers, ex)

    private fun assembleExceptionResponse(
        httpStatus: HttpStatus,
        headers: HttpHeaders,
        ex: MethodArgumentNotValidException
    ): ResponseEntity<Any> =
        ResponseEntity.status(httpStatus).headers(headers).body(assembleApiException(httpStatus, ex))

    private fun assembleApiException(httpStatus: HttpStatus, ex: MethodArgumentNotValidException) =
        ApiException(httpStatus, ex.localizedMessage, collectErrors(ex))

    private fun collectErrors(ex: MethodArgumentNotValidException): Map<String, List<String?>> =
        collectGlobalErrors(ex).plus(collectFieldErrors(ex))

    private fun collectGlobalErrors(ex: MethodArgumentNotValidException): Map<String, List<String?>> =
        ex.bindingResult.globalErrors.groupBy({ it.objectName }, { it.defaultMessage })

    private fun collectFieldErrors(ex: MethodArgumentNotValidException): Map<String, List<String?>> =
        ex.bindingResult.fieldErrors.groupBy({ "${it.objectName}.${it.field}" }, { it.defaultMessage })

}
