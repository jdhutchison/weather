package sh.hutch.weather

import org.jsoup.Jsoup
import org.jsoup.nodes.Element
import java.time.*
import java.time.format.DateTimeFormatter
import javax.inject.Singleton

/**
 * Parses data from the Bureau of Meterology site.
 */
@Singleton
class BomParser {
	
	val timestampParser = DateTimeFormatter.ofPattern("h:mm a d MMMM yyyy")
	val dateParser = DateTimeFormatter.ofPattern("EEEE d MMMM yyyy")
	val timezone = ZoneId.of("Australia/Melbourne")
	
	/**
	 * Parses html into a list of forcasts, sorted by date, with the current
	 * date's forecast being the first element.
	 * @param html  the html from a BoM forcast page.
	 */
	fun parseForecasts(html: String): List<Forecast> {
		val forecasts = mutableListOf<Forecast>()
		val document = Jsoup.parse(html)
		
		// Extract the forecast timestamp
		val dateText = document.selectFirst("p.date").text()
		val timestamp = parseTimestamp(dateText)
		
		// Handle the current day
		
		// Future days
		val futureForecasts = document.select("div.day").filter { !it.hasClass("eve") }
		
		forecasts.addAll(futureForecasts.map { parseOneForecast(it, timestamp) }.sortedBy { it.date })
		return forecasts
	}
	
	/**
	 * Parses the forecast for one day from a DOM element.
	 */
	internal fun parseOneForecast(node: Element, timestamp: Instant): Forecast {
		val min = node.selectFirst("em.min").text().toInt()
		val max = node.selectFirst("em.max").text().toInt()
		val dateData = node.selectFirst("h2").text()
		val summary = node.selectFirst("dd.summary").text()
		val forecast = node.selectFirst("p").text()
		val rainProbability = extractNumbers(node.selectFirst("em.pop").text())[0].toInt()
		val rainRange = if(node.select("em.rain").isEmpty()) {
		  Pair<Double?, Double?>(null, null)
		} else {
			val range = extractNumbers(node.selectFirst("em.rain").text())
			Pair<Double?, Double?>(range[0], range[1])
		}
		
		// Determine the date
		val year = determineYear(dateData, LocalDate.now())
		val date = LocalDate.parse("$dateData $year", dateParser)
		
		return Forecast(dateData.split(" ")[0], date, min, max, summary, forecast,
			rainRange.first, rainRange.second, rainProbability, timestamp)
	}
	
	/**
	 * Parses the forecast for the current day from a DOM element.
	 */
	internal fun parseTodaysForecast(node: Element) = ""
	
	/**
	 * Parses the BoM timestamp on the page and turns it into an instant.
	 * Timestamps going in look like "Forecast issued at 4:20 pm EDT on Saturday 6 March 2021."
	 */
	fun parseTimestamp(ts: String): Instant {
		val tokens = ts.split(' ', '.')
		val neededTokens = tokens.subList(3, 5) + tokens.subList(8, tokens.size)
		val time = LocalDateTime.parse(neededTokens.joinToString(" ").trim(), timestampParser)
		return ZonedDateTime.of(time, timezone).toInstant()
	}
	
	fun determineYear(date: String, today: LocalDate): Int {
		val currentYear = today.year
		val isDecember = (today.monthValue == 12)
		return if (isDecember && date.contains("January")) {
			currentYear + 1
		} else {
			currentYear
		}
	}
}
