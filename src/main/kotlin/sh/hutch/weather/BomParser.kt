package sh.hutch.weather

import org.jsoup.Jsoup
import org.jsoup.nodes.Element
import javax.inject.Singleton

/**
 * Parses data from the Bureau of Meterology site.
 */
@Singleton
object BomParser {
	
	/**
	 * Parses html into a list of forcasts, sorted by date, with the current
	 * date's forecast being the first element.
	 * @param html the html from a BoM forcast page. 
	 */
	fun parseForcasts(html: String): List<Forecast> {
		val forecasts = mutableListOf<Forecast>()
		val document = Jsoup.parse(html)
		
		// Extract the forecast timestamp
		val dateText = document.select("p.date").text()
		// Handle the current day
		
		// Future days
		val futureForecasts = document.select("div.day.main")
		//forecasts.addAll(futureForecasts.map { parseOneForecast(it) })
		return forecasts
	}
	
	/**
	 * Parses the forecast for one day from a DOM element.
	 *
	internal fun parseOneForecast(node: Element): Forecast {
		return Forecast()
	}*/
	
	/**
	 * Parses the forecast for the current day from a DOM element.
	 */
	internal fun parseTodaysForecast(node: Element) = ""
}
