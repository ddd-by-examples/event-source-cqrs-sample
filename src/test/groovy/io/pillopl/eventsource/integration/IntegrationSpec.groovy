package io.pillopl.eventsource.integration

import groovy.transform.CompileStatic
import io.pillopl.eventsource.Application
import org.javers.common.date.DateProvider
import org.javers.core.Javers
import org.javers.core.JaversBuilder
import org.springframework.boot.test.SpringApplicationContextLoader
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.EnableAspectJAutoProxy
import org.springframework.context.annotation.Primary
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.web.WebAppConfiguration
import spock.lang.Specification

import java.time.LocalDateTime

@ContextConfiguration(classes = [Application], loader = SpringApplicationContextLoader)
@CompileStatic
@WebAppConfiguration
@EnableAspectJAutoProxy(proxyTargetClass = true)
@ActiveProfiles("test")
@Configuration
class IntegrationSpec extends Specification {

    static MockClock mockClock = new MockClock()

    def setup() {
        mockClock.reset()
    }

    @Bean
    @Primary
    Javers javers() {
        DateProvider dateProvider = new DateProvider() {
            @Override
            LocalDateTime now() {
                return mockClock.now()
            }
        }

        return JaversBuilder.javers().withDateTimeProvider(dateProvider).build()
    }
}
