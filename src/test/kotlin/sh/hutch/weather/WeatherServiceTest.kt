package sh.hutch.weather

import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import org.junit.jupiter.api.Test
import javax.inject.Inject

class WeatherServiceTest {
	
	val service: WeatherService

	val store = WeatherDataStore()
	val client = StubBomClient()

	init {
		service = WeatherService(store, client, BomParser())
		store.maxObservableAge = "PT24H"
	}
	
	@Test
	fun testGetLatestObservations() {
		service.getLatestObservations()
	}
}
