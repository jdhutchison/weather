package sh.hutch.weather

import io.micronaut.http.MediaType
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get

@Controller
class WeatherController {
	
	@Get("/")
	fun getCurrent() = "Weather!"
	
	fun getToday() = "Today"
	
	
}