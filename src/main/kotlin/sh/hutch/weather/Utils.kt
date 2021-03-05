package sh.hutch.weather

import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.util.regex.Pattern

val NUMBER_PATTERN = Pattern.compile("-?\\d+(\\.\\d+)?")
val BOM_TIMESTAMP_PARSER = DateTimeFormatter.ofPattern("yyyyMMddHHmmss")

/**
 * Extracts all the numbers contained within a
 */
fun extractNumbers(s: String): Array<Double> {
  val matcher = NUMBER_PATTERN.matcher(s)
  val numbers = mutableListOf<Double>()
  while (matcher.find()) {
    numbers.add(matcher.group().toDouble())
  }
  return numbers.toTypedArray()
}

/**
 * Converts a BoM style timestamp as used in their JSON feed to an Instant.
 */
fun parseBoMUtcTimestamp(timestamp: String) =
  LocalDateTime.parse(timestamp, BOM_TIMESTAMP_PARSER).atOffset(ZoneOffset.UTC).toInstant()


