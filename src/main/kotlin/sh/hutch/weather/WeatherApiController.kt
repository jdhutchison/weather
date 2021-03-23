package sh.hutch.weather

import io.micronaut.http.MediaType
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get
import io.micronaut.http.annotation.QueryValue

/**
 * This controller is focused on returning data as JSON for different
 * types of requests. In essence it implements the API part.
 */

@Controller("/api")
class WeatherApiController(val dataStore: WeatherDataStore) {
	
	@Get("/", produces = [MediaType.APPLICATION_JSON])
	fun getCurrent() = dataStore.getDataForNow()
	
	@Get("/today", produces = [MediaType.APPLICATION_JSON])
	fun getToday() = dataStore.forecasts[0]

	@Get("/forecasts", produces = [MediaType.APPLICATION_JSON])
	fun getForcast(@QueryValue(defaultValue = "7") days: Int) = dataStore.getDaysOfForecast(days)
}
