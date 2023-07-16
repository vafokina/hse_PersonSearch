package hse.personsearch

import hse.personsearch.config.ApplicationProperties
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.runApplication

@SpringBootApplication() //exclude = [DataSourceAutoConfiguration::class]
@EnableConfigurationProperties(ApplicationProperties::class)
open class PersonsearchApplication

fun main(args: Array<String>) {
    runApplication<PersonsearchApplication>(*args)
}
