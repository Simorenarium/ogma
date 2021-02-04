package coffee.michel.ogma.exception

import org.springframework.http.HttpStatus

internal data class ApiException(
    val status: HttpStatus,
    val message: String,
    var errors: Map<String, List<String?>>
)
