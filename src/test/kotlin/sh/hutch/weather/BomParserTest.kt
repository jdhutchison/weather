package sh.hutch.weather

import org.junit.jupiter.api.Test
import java.io.BufferedReader
import java.nio.file.Files
import java.time.LocalDate
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
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
		val forecasts = parser.parseForecasts(html)
		assertEquals(7, forecasts.size)
		
		// Check each forecast has main details set
		for (f in forecasts) {
			assertNotNull(f.day)
			assertNotNull(f.date)
			assertNotNull(f.min)
			assertNotNull(f.max)
			assertNotNull(f.summary)
			assertNotNull(f.forecast)
			assertNotNull(f.rainfallProbability)
		}
		
		// Verify a known day
		val tuesday = forecasts.first { it.day == "Tuesday"}
		assertEquals(LocalDate.of(2021, 3, 9), tuesday.date)
		assertEquals(15, tuesday.min)
		assertEquals(20, tuesday.max)
		assertEquals(0.0, tuesday.rainfallLow)
		assertEquals(0.2, tuesday.rainfallHigh)
		assertEquals(30, tuesday.rainfallProbability)
		assertEquals("Cloudy.", tuesday.summary)
		
	}
	
	internal fun readFile(relPath: String): String {
		val stream = this.javaClass.classLoader.getResourceAsStream(relPath)
		return stream.bufferedReader().use(BufferedReader::readText)
	}
}