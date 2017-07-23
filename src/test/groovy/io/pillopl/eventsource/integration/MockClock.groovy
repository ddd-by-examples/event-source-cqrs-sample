package io.pillopl.eventsource.integration

import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.concurrent.LinkedBlockingQueue

import static java.time.LocalDate.now
import static java.time.ZoneId.systemDefault
import static java.time.temporal.ChronoUnit.DAYS

/**
 * @author pawel szymczyk
 */
class MockClock {

    static final Instant TODAY = now().atStartOfDay(systemDefault()).toInstant()
    static final Instant TOMORROW = TODAY.plus(1, DAYS)
    static final Instant DAY_AFTER_TOMORROW = TOMORROW.plus(1, DAYS)

    private final Queue<LocalDateTime> clock

    MockClock() {
        clock = new LinkedBlockingQueue<>()
        clock.add(TODAY)
    }

    LocalDateTime now() {
        return clock.size() > 1 ? clock.poll() : clock.peek()
    }

    def mock(Instant instant) {
        mock(LocalDateTime.ofInstant(instant, ZoneId.systemDefault()))
    }

    def mock(LocalDateTime localDateTime) {
        clock.add(localDateTime)
    }

    void reset() {
        clock.clear()
        clock.add(LocalDateTime.ofInstant(TODAY, ZoneId.systemDefault()))
    }

    def clear() {
        clock.clear()
    }
}
