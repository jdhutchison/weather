package sh.hutch.weather

import io.micronaut.http.MediaType
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get
import io.micronaut.http.annotation.QueryValue

@Controller
class WeatherController(val dataStore: WeatherDataStore) {
	
	@Get("/", produces = [MediaType.APPLICATION_JSON])
	fun getCurrent() = dataStore.getDataForNow()
	
	@Get("/", produces = [MediaType.APPLICATION_JSON])
	fun getToday() = "Today"

	@Get("/forecast")
	fun getForcast(@QueryValue(defaultValue = "7") days: Int) = dataStore.getDaysOfForecast(days)
}
