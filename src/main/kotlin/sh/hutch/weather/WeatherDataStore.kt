package sh.hutch.weather

import javax.inject.Singleton

/**
 * The data store is simply a holder of data held in
 * memory.
 */
@Singleton
object WeatherDataStore {
	var currentWeather: Now? = null
	var forecasts: List<Forecast> = emptyList()
}