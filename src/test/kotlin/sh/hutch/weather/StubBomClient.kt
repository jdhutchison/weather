package sh.hutch.weather

import com.fasterxml.jackson.databind.ObjectMapper
import io.micronaut.jackson.ObjectMapperFactory
import java.io.BufferedReader

/**
 * A BomClient implementation that returns stub data from files to make tesing easier.
 */
class StubBomClient: BomClient() {

    val mapper = ObjectMapper()

    override fun getObservations() = mapper.readValue(readFile("testdata/observations.json"),
        BomObservationResponse::class.java)!!

    override fun getForecasts() = readFile("testdata/forecasts.html")


    internal fun readFile(relPath: String): String {
        val stream = this.javaClass.classLoader.getResourceAsStream(relPath)
        return stream.bufferedReader().use(BufferedReader::readText)
    }
}
