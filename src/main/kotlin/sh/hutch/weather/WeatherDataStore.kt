package sh.hutch.weather

import io.micronaut.context.annotation.Value
import java.time.Duration
import java.time.Instant
import javax.inject.Singleton
import kotlin.streams.toList

/**
 * The data store is simply a holder of data held in memory.
 *
 * This implementation isn't threadsafe. A possible future improvement.
 */
@Singleton
object WeatherDataStore {

	val OBSERVATION_SORTED = Comparator<Observation> { t, t2 ->  if (t.timestamp.isAfter(t2.timestamp)) 1 else 0 }

	@Value("\${weather.observations.keep-for}")
	lateinit var maxObservableAge: String

	var observations: List<Observation> = emptyList()
	var forecasts: List<Forecast> = emptyList()

	fun getDaysOfForecast(days: Int) = forecasts.subList(0, days)

	fun addObservations(newObservations: List<Observation>) {
		// First trim observations older than threshold
		val threshold = Instant.now().minus(Duration.parse(maxObservableAge))
		val notTooOld = observations.filterNot { it.timestamp.isAfter(threshold) }
		observations = (newObservations + notTooOld).stream().sorted(OBSERVATION_SORTED).toList()
	}

	fun getDataForNow() = Today(observations[0], forecasts[0])
}
