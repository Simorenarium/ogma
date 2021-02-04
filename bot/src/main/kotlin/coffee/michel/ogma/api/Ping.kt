package coffee.michel.ogma.api

import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping(
    value = ["/ping"],
    produces = [MediaType.APPLICATION_JSON_VALUE]
)
class Ping {

    @GetMapping
    fun get() = "Pong!"

}