package sh.hutch.weather

import io.micronaut.http.HttpRequest
import io.micronaut.http.client.HttpClient
import io.micronaut.http.client.annotation.Client
import io.micronaut.scheduling.annotation.Scheduled
import java.time.Duration
import java.time.Instant
import java.util.Arrays
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WeatherService(val dataStore: WeatherDataStore) {

    @Client("http://www.bom.gov.au")
    @Inject
    lateinit var httpClient: HttpClient

    @Scheduled(fixedRate = "\${weather.forecasts.fetch-rate}")
    fun getForecasts() {

    }

    @Scheduled(fixedRate = "\${weather.observations.fetch-rate}")
    fun getLatestObservations() {
        // If we don't have any existing observations then get any observation from the last hour
        val mostRecentObservation = if (dataStore.observations.isNotEmpty()) {
            dataStore.observations[0].timestamp
        } else {
            Instant.now().minus(Duration.ofHours(1))
        }
        val response = httpClient.toBlocking().exchange(
            HttpRequest.GET<BomObservationResponse>("/fwo/IDV60901/IDV60901.95936.json"), BomObservationResponse::class.java)
        val bomResponse = response.body()!!

        val observations = bomResponse.observations.data.filter {
            parseBoMUtcTimestamp(it.aifstime_utc).isAfter(mostRecentObservation)
        }.map { it.toObservation() }
        dataStore.addObservations(observations)
    }
}
