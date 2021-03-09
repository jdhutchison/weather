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
	 * Parses html into a list of forcasts, sorted by date, along with the forecast update
	 * for the current day.
	 * @param html  the html from a BoM forcast page.
	 * @return a pair with the update as the first element and the sorted forecasts as the second.
	 */
	fun parseForecasts(html: String): Pair<ForecastUpdate, List<Forecast>> {
		val forecasts = mutableListOf<Forecast>()
		val document = Jsoup.parse(html)
		
		// Extract the forecast timestamp
		val dateText = document.selectFirst("p.date").text()
		val timestamp = parseTimestamp(dateText)
		val forecastDomNodes = document.select("div.day")
		
		// Handle the current day
		val todaysUpdateElement = forecastDomNodes.first { forecastFilter(it) }
		val todaysUpdate = parseTodaysForecast(todaysUpdateElement, timestamp)
		
		// Future days - removing the current day
		forecasts.addAll(forecastDomNodes.filterNot { forecastFilter(it) }
			.map { parseOneForecast(it, timestamp) }.sortedBy { it.date })
		return Pair(todaysUpdate, forecasts)
	}

	/**
	 * A filter to select (or exclude) the updated forecast for the current day from
	 * the nodes for forecasts on future days.
	 */
	internal fun forecastFilter(node: Element) = node.selectFirst("h2").text().startsWith("Forecast for the rest of ")
	
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
		val rainRange = calculateRainSpread(node)
		
		// Determine the date
		val year = determineYear(dateData, LocalDate.now())
		val date = LocalDate.parse("$dateData $year", dateParser)
		
		return Forecast(dateData.split(" ")[0], date, min, max, summary, forecast,
			rainRange.first, rainRange.second, rainProbability, timestamp)
	}

	internal fun calculateRainSpread(node: Element) = node.selectFirst("em.rain")?.let {
		val range = extractNumbers(it.text())
		Pair<Double?, Double?>(range[0], range[1])
	} ?: Pair<Double?, Double?>(null, null)


	/**
	 * Parses the forecast for the current day from a DOM element.
	 */
	internal fun parseTodaysForecast(node: Element, timestamp: Instant): ForecastUpdate {
		val min = node.selectFirst("em.min")?.let { it.text().toInt() }
		val max = node.selectFirst("em.max")?.let { it.text().toInt() }
		val summary = node.selectFirst("dd.summary")?.let { it.text() }
		val forecast = node.selectFirst("p")?.let { it.text() }
		val rainRange = calculateRainSpread(node)
		val rainProbability = node.selectFirst("em.pop")?.let { extractNumbers(it.text())[0].toInt() }
		return ForecastUpdate(min, max, summary, forecast, rainRange.first, rainRange.second,
			rainProbability, timestamp)
	}

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

	/**
	 * Determines the year to use to be able to generate the correct date for the timestamps.
	 * If its December and the forecast references January then it's for next year, otherwise
	 * this year.
	 */
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
