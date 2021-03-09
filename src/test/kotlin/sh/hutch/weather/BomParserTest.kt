package sh.hutch.weather

import org.junit.jupiter.api.Test
import java.io.BufferedReader
import java.nio.file.Files
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class BomParserTest {
	
	val parser = BomParser()
	
	@Test
	fun `determineYear should use current year for non-Dec Jan pairings`() {
	  val janDate = "Friday 22 January"
	  val nonJanDate = "Friday 19 March"
		
		for (month in 1..11) {
			val fakeToday = LocalDate.of(2021, month, 1)
			assertEquals(fakeToday.year, parser.determineYear(janDate, fakeToday))
		}
		
		for (month in 1..12) {
			val fakeToday = LocalDate.of(2021, month, 1)
			assertEquals(fakeToday.year, parser.determineYear(nonJanDate, fakeToday))
		}
	}
	
	@Test
	fun `determineYear should use next year for a December today and January date`() {
		val janDate = "Friday 22 January"
		val fakeToday = LocalDate.of(2021, 12, 31)
		assertEquals(fakeToday.year + 1, parser.determineYear(janDate, fakeToday))
	}
	
	@Test
	fun `parseForecasts correctly processes future forecasts`() {
		val html = readFile("testdata/forecasts.html")
		val forecastData = parser.parseForecasts(html)
		assertEquals(7, forecastData.second.size)

		val expectedTimestamp = LocalDateTime.of(2021, 3, 4, 16, 20, 0, 0)
			.atZone(ZoneId.of("Australia/Melbourne")).toInstant()
		
		// Check each forecast has main details set
		for (f in forecastData.second) {
			assertNotNull(f.day)
			assertNotNull(f.date)
			assertNotNull(f.min)
			assertNotNull(f.max)
			assertNotNull(f.summary)
			assertNotNull(f.forecast)
			assertNotNull(f.rainfallProbability)
			assertEquals(expectedTimestamp, f.timestamp)
		}
		
		// Verify a known day
		val tuesday = forecastData.second.first { it.day == "Tuesday"}
		assertEquals(LocalDate.of(2021, 3, 9), tuesday.date)
		assertEquals(15, tuesday.min)
		assertEquals(20, tuesday.max)
		assertEquals(0.0, tuesday.rainfallLow)
		assertEquals(0.2, tuesday.rainfallHigh)
		assertEquals(30, tuesday.rainfallProbability)
		assertEquals("Cloudy.", tuesday.summary)
		
	}

	@Test
	fun `parseForecasts correctly parse evening forecast update for today`() {
		val html = readFile("testdata/forecasts.html")
		val forecastData = parser.parseForecasts(html)
		val forecastItself = "Cloudy. High (60%) chance of light showers or drizzle about the eastern suburbs " +
				"later this evening, grading to a slight (20%) chance in the west. Winds southwesterly 15 to 25 km/h."

		assertEquals("Cloudy.", forecastData.first.summary)
		assertEquals(forecastItself, forecastData.first.forecast)
		assertEquals(40, forecastData.first.rainfallProbability)

		// Data not present
		assertNull(forecastData.first.min)
		assertNull(forecastData.first.max)
		assertNull(forecastData.first.rainfallLow)
		assertNull(forecastData.first.rainfallHigh)
	}

	@Test
	fun `parseForecasts correctly parse forecast update during day for today`() {
		val html = readFile("testdata/forecasts-update-as-main.html")
		val forecastData = parser.parseForecasts(html)
		val forecastItself = "Mostly cloudy. The chance of fog in the southeast suburbs early this morning. " +
				"High (60%) chance of light showers about the eastern suburbs grading to a slight (30%) chance " +
				"in the west. Sunny breaks increasing later in the afternoon. Light winds becoming southwesterly " +
				"15 to 20 km/h in the morning then tending southerly in the middle of the day."

		assertEquals("Possible shower.", forecastData.first.summary)
		assertEquals(forecastItself, forecastData.first.forecast)
		assertEquals(40, forecastData.first.rainfallProbability)
		assertEquals(20, forecastData.first.max)
		assertEquals(0.0, forecastData.first.rainfallLow)
		assertEquals(1.0, forecastData.first.rainfallHigh)

		// Data not present
		assertNull(forecastData.first.min)
	}
	
	internal fun readFile(relPath: String): String {
		val stream = this.javaClass.classLoader.getResourceAsStream(relPath)
		return stream.bufferedReader().use(BufferedReader::readText)
	}
}
