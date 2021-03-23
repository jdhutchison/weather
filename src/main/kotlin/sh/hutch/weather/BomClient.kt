package sh.hutch.weather

import io.micronaut.context.annotation.Value
import io.micronaut.http.HttpRequest
import io.micronaut.http.client.HttpClient
import io.micronaut.http.client.annotation.Client
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Handles all the logic around communicating with the Bureau of Meteorology website.
 *
 * This class is open, as are its functions, as there's a stub/testing implementation.
 */
@Singleton
open class BomClient {

    companion object {
        val BOM_URL = "http://www.bom.gov.au"
    }

    @Client("http://www.bom.gov.au")
    @Inject
    lateinit var httpClient: HttpClient

    @Value("\${weather.observations.referrer}")
    lateinit var referrer: String

    @Value("\${weather.observations.user-agent}")
    lateinit var userAgent: String

    open fun getObservations() = httpClient.toBlocking().exchange(
        HttpRequest.GET<BomObservationResponse>("$BOM_URL/fwo/IDV60901/IDV60901.95936.json")
            .header("Referer", referrer).header("User-Agent", userAgent), BomObservationResponse::class.java).body()!!

    open fun getForecasts() =
        httpClient.toBlocking().exchange(HttpRequest.GET<String>("/vic/forecasts/melbourne.shtml").
        header("Referer", referrer).header("User-Agent", userAgent), String::class.java).body()!!
}
