package io.pillopl.eventsource.integration

import groovy.transform.CompileStatic
import io.pillopl.eventsource.Application
import org.springframework.boot.test.SpringApplicationContextLoader
import org.springframework.context.annotation.EnableAspectJAutoProxy
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.web.WebAppConfiguration
import spock.lang.Specification

@ContextConfiguration(classes = [Application], loader = SpringApplicationContextLoader)
@CompileStatic
@WebAppConfiguration
@EnableAspectJAutoProxy(proxyTargetClass = true)
@ActiveProfiles("test")
class IntegrationSpec extends Specification {
}
