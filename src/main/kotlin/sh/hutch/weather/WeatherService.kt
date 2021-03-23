package sh.hutch.weather


import io.micronaut.scheduling.annotation.Scheduled
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.time.Duration
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.TextStyle.FULL_STANDALONE
import java.util.*
import javax.inject.Singleton

@Singleton
class WeatherService(val dataStore: WeatherDataStore, val client: BomClient, val parser: BomParser) {

    private val LOG: Logger = LoggerFactory.getLogger(WeatherService::class.java)

    /**
     * Used to force the service to fetch all the data fresh.
     */
    fun fetchAllData() {
        LOG.info("Unscheduled fetching of all data.")
        getForecasts()
        getLatestObservations()
    }

    @Scheduled(fixedRate = "\${weather.forecasts.fetch-rate}")
    fun getForecasts() {
      LOG.trace("Fetching forecasts.")

      try {
          val html = client.getForecasts()
          val updateAndForecasts = parser.parseForecasts(html)

          // If there's no forecast so far for today we need to make one from the update.
          val updatedToday = if (dataStore.forecasts.isNullOrEmpty()) {
              makeForecastFromUpdate(updateAndForecasts.first)
          } else {
              mergeForecastWithUpdate(dataStore.forecasts[0], updateAndForecasts.first)
          }
          LOG.debug("Updating data store with forecasts for today and ${updateAndForecasts.second.size} other days.")
          LOG.debug("Forecasts produced by BoM at " + updateAndForecasts.first.timestamp.atZone(ZoneId.systemDefault()))
          dataStore.forecasts = listOf(updatedToday) + updateAndForecasts.second
      } catch (e: Exception) {
          LOG.error("Error while fetching forecasts from BoM site.", e)
      }
    }

    @Scheduled(fixedRate = "\${weather.observations.fetch-rate}")
    fun getLatestObservations() {
        // If we don't have any existing observations then get any observation from the last hour
        val mostRecentObservation = if (dataStore.observations.isNotEmpty()) {
            dataStore.observations[0].timestamp
        } else {
            Instant.now().minus(Duration.ofHours(1))
        }
        LOG.debug("Fetching observations. Looking for observations more recent than " +
                mostRecentObservation.atZone(ZoneId.of("Australia/Melbourne")))

        try {
            val response = client.getObservations()
            val observations = response.observations.data.filter {
                parseBoMUtcTimestamp(it.aifstime_utc).isAfter(mostRecentObservation)
            }.map { it.toObservation() }
            LOG.debug("Fetched recent observations. Adding ${observations.size} to data.")
            dataStore.addObservations(observations)
        } catch (e: Exception) {
            LOG.error("Error during observation loading.", e)
        }
    }

    internal fun mergeForecastWithUpdate(forecast: Forecast, update: ForecastUpdate): Forecast {
        // Test each field to see if the update has any values
        val min = update.min ?: forecast.min
        val max = update.max ?: forecast.max
        val summary = update.summary ?: forecast.summary
        val fc = update.forecast ?: forecast.forecast
        val rainProb = update.rainfallProbability ?: forecast.rainfallProbability
        val rainLow = update.rainfallLow ?: forecast.rainfallLow
        val rainHigh = update.rainfallHigh ?: forecast.rainfallHigh
        return forecast.copy(min = min, max = max, summary = summary, forecast = fc, rainfallProbability = rainProb,
            rainfallLow = rainLow, rainfallHigh = rainHigh, timestamp = update.timestamp)
    }

    internal fun makeForecastFromUpdate(update: ForecastUpdate): Forecast {
        val min = update.min ?: 0
        val max = update.max ?: 50
        val summary = update.summary ?: "Unknown"
        val fc = update.forecast ?: "Unknown"
        val rainProb = update.rainfallProbability ?: 0
        val rainLow = update.rainfallLow ?: 0.0
        val rainHigh = update.rainfallHigh ?: 0.0
        val date = LocalDate.now()
        val dayOfWeek = date.dayOfWeek.getDisplayName(FULL_STANDALONE, Locale.getDefault())
        return Forecast(dayOfWeek, date, min, max, summary, fc, rainLow, rainHigh, rainProb, update.timestamp)
    }
}
