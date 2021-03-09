package sh.hutch.weather

import java.time.Instant
import java.time.LocalDate

/**
 * Contains all the models that are used to store and send data.
 */

data class Wind(val speed: Double, val direction: String, val gustSpeed: Double)

data class Observation(val temp: Double, val apparentTemp: Double, val humidity: Int, val pressure: Double,
                       val rainfall: Double, val wind: Wind?, val timestamp: Instant)

data class Forecast(val day: String, val date: LocalDate, val min: Int, val max: Int,
                    val summary: String, val forecast: String, val rainfallLow: Double?, val rainfallHigh: Double?,
                    val rainfallProbability: Int?, val timestamp: Instant)

/**
 * An updated forecast for the current day which might be missing some details depending on when it was
 * issued. That's why every field is nullable.
 */
data class ForecastUpdate(val min: Int? = null, val max: Int? = null, val summary: String? = null,
                           val forecast: String? = null, val rainfallLow: Double? = null, val rainfallHigh: Double? = null,
                           val rainfallProbability: Int? = null, val timestamp: Instant)

/** Aggregates current data about today, with the forecast. */
data class Today(val currently: Observation, val forecast: Forecast)

