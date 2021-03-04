package sh.hutch.weather

import java.time.Instant
import java.time.LocalDate

/**
 * Contains all the models that are used to store
 * and send data.
 */

data class Wind(val speed: Double, val direction: String, val gustSpeed: Double)

data class Now(val temp: Double, val apparentTemp: Double, val humidity: Int, val rainfall: Double, val timestamp: Instant)

data class Forecast(val day: String, val date: LocalDate, val min: Double, val max: Double,
                    val summary: String, val forecast: String, val rainfallLow: Int?, val rainfallHigh: Int?,
                    val rainfallProbability: Int?, val timestamp: Instant)

/** Aggregates current data about today, with the forecast. */
data class Today(val currently: Now, val forecast: Forecast)