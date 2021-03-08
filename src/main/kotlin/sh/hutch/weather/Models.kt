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

/** Aggregates current data about today, with the forecast. */
data class Today(val currently: Observation, val forecast: Forecast)

data class BomObservationResponse(val observations: BomObservationMeta)

data class BomObservationMeta(val data: List<BomObservation>)

data class BomObservation(
  val sort_order: Int,
  val wmo: Int,
  val name: String,
  val history_product: String,
  val local_date_time: String,
  val local_date_time_full: String,
  val aifstime_utc: String,
  val lat: Double,
  val lon: Double,
  val apparent_t: Double,
  val cloud: String,
  val cloud_base_m: String?,
  val cloud_oktas: String?,
  val cloud_type_id: String?,
  val cloud_type: String,
  val delta_t: Double,
  val gust_kmh: Double,
  val gust_kt: Double,
  val air_temp: Double,
  val dewpt: Double,
  val press: Double,
  val press_qnh: Double,
  val press_msl: Double,
  val press_tend: String,
  val rain_trace: String,
  val rel_hum: Double,
  val sea_state: String,
  val swell_dir_worded: String,
  val swell_height: Any?,
  val swell_period: Any?,
  val vis_km: String,
  val weather: String,
  val wind_dir: String,
  val wind_spd_kmh: Double,
  val wind_spd_kt: Double
) {

  /** Converts BoM observation object into project one */
  fun toObservation() = Observation(air_temp, apparent_t, rel_hum.toInt(), press_msl, rain_trace.toDouble(),
      Wind(wind_spd_kmh, wind_dir, gust_kmh), parseBoMUtcTimestamp(aifstime_utc))
}
