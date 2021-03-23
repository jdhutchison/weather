package sh.hutch.weather

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

/**
 * These data classes are POJOs to unmarshall JSON data from BoM into for easier use.
 */

@JsonIgnoreProperties(ignoreUnknown = true)
class BomObservationResponse {
    lateinit var observations: BomObservationMeta
}

@JsonIgnoreProperties(ignoreUnknown = true)
class BomObservationMeta {
    lateinit var data: List<BomObservation>
}

@JsonIgnoreProperties(ignoreUnknown = true)
class BomObservation {
    var sort_order: Int = 0
    var wmo: Int = 0
    lateinit var name: String
    lateinit var history_product: String
    lateinit var local_date_time: String
    lateinit var local_date_time_full: String
    lateinit var aifstime_utc: String
    var lat: Double = 0.0
    var lon: Double = 0.0
    var apparent_t: Double = 0.0
    var delta_t: Double = 0.0
    var gust_kmh: Double = 0.0
    var gust_kt: Double = 0.0
    var air_temp: Double = 0.0
    var dewpt: Double = 0.0
    var press: Double = 0.0
    var press_qnh: Double = 0.0
    var press_msl: Double = 0.0
    lateinit var press_tend: String
    lateinit var rain_trace: String
    var rel_hum: Double = 0.0
    lateinit var vis_km: String
    lateinit var weather: String
    lateinit var wind_dir: String
    var wind_spd_kmh: Double = 0.0

    /** Converts BoM observation object into project one */
    fun toObservation() = Observation(air_temp, apparent_t, rel_hum.toInt(), press_msl, rain_trace.toDouble(),
        Wind(wind_spd_kmh, wind_dir, gust_kmh), parseBoMUtcTimestamp(aifstime_utc))
}
