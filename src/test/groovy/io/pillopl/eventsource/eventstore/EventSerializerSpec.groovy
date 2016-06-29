package io.pillopl.eventsource.eventstore

import io.pillopl.eventsource.domain.shopitem.events.ItemBought
import io.pillopl.eventsource.domain.shopitem.events.ItemPaid
import io.pillopl.eventsource.domain.shopitem.events.ItemPaymentTimeout
import spock.lang.Specification
import spock.lang.Subject

import static java.time.Instant.now

class EventSerializerSpec extends Specification {

    final String ANY_TYPE = "ANY_TYPE"
    final String ANY_UUID = "9a94d251-5fdb-4f38-b308-9f72d2355467"

    @Subject
    EventSerializer eventSerializer = new EventSerializer()

    def "should parse ItemBought event"() {
        given:
            String body = """{
                "type": "$ItemBought.TYPE",
                "uuid": "$ANY_UUID",
                "when": "2016-05-24T12:06:41.045Z"
                }"""
        when:
            ItemBought event = eventSerializer.deserialize(new EventDescriptor(body, now(), ANY_TYPE))
        then:
            event.uuid.toString() == ANY_UUID
    }

    def "should parse ItemPaid event"() {
        given:
            String body = """{
                "type": "$ItemPaid.TYPE",
                "uuid": "$ANY_UUID",
                "when": "2016-05-24T12:06:41.045Z"
                }"""
        when:
            ItemPaid event = eventSerializer.deserialize(new EventDescriptor(body, now(), ANY_TYPE))
        then:
            event.uuid.toString() == ANY_UUID
    }

    def "should parse ItemPaymentTimeout event"() {
        given:
            String body = """{
                "type": "$ItemPaymentTimeout.TYPE",
                "uuid": "$ANY_UUID",
                "when": "2016-05-24T12:06:41.045Z"
                }"""
        when:
            ItemPaymentTimeout event = eventSerializer.deserialize(new EventDescriptor(body, now(), ANY_TYPE))
        then:
            event.uuid.toString() == ANY_UUID
    }

}
