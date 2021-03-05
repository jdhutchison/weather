package sh.hutch.weather

import org.junit.jupiter.api.Test
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.*
import java.time.ZoneId
import java.time.ZonedDateTime

class UtilsTest {

  @Test
  fun `extractNumbers should extract numbers`() {
    assertThat(extractNumbers(""), array())
    // Test regular decimal numbers
    assertThat(extractNumbers("1ddsdf46fergr99 44.01.2"), arrayContaining(1.0, 46.0, 99.0, 44.01, 2.0))
    // Test negative numbers
    assertThat(extractNumbers("-1ddsdf-46fergr-99 -44.01-2"), arrayContaining(-1.0, -46.0, -99.0, -44.01, -2.0))
    // Test numbers as whole words
    assertThat(extractNumbers("-1 46 -99 44.01 -2"), arrayContaining(-1.0, 46.0, -99.0, 44.01, -2.0))
  }

  @Test
  fun `parseBoMUtcTimestamp should correctly parse timestamps`() {
    val utcTime = ZonedDateTime.of(2021, 3, 5, 1, 0, 0, 0, ZoneId.of("UTC")).toInstant()
    val parsedTime = parseBoMUtcTimestamp("20210305010000")
    assertThat(parsedTime, equalTo(utcTime))
  }
}
