package coffee.michel.ogma

import java.math.BigDecimal
import java.time.DayOfWeek
import java.time.ZonedDateTime
import java.util.*

fun <T> Optional<T>.unwrap(): T? = orElse(null)

fun Long.isGreaterThan(other: Long): Boolean {
    return this > other
}

fun BigDecimal.isNonNegative(): Boolean {
    return this >= BigDecimal.ZERO
}
