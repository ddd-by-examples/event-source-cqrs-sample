package io.pillopl.eventsource.integration.readmodel

import io.pillopl.eventsource.boundary.ShopItems
import io.pillopl.eventsource.integration.IntegrationSpec
import io.pillopl.eventsource.readmodel.JdbcReadModel
import io.pillopl.eventsource.readmodel.PaymentTimeoutChecker
import org.junit.Ignore
import org.springframework.beans.factory.annotation.Autowired
import spock.lang.Subject

import static io.pillopl.eventsource.CommandFixture.buyItemCommand
import static io.pillopl.eventsource.CommandFixture.payItemCommand
import static java.time.Instant.now
import static java.time.temporal.ChronoUnit.HOURS

@Ignore
class PaymentTimeoutCheckerIntegrationSpec extends IntegrationSpec {

    private static final UUID WILL_TIMEOUT = UUID.randomUUID()
    private static final UUID WILL_ALSO_TIMEOUT = UUID.randomUUID()
    private static final UUID WILL_NOT_TIMEOUT = UUID.randomUUID()
    private static final UUID ANY_UUID = UUID.randomUUID()


    @Autowired
    @Subject
    PaymentTimeoutChecker paymentTimeoutChecker

    @Autowired
    ShopItems shopItems

    @Autowired
    JdbcReadModel readModel

    def 'should mark item as payment missing'() {
        given:
            shopItems.buy(buyItemCommand(WILL_TIMEOUT, now().minus(25, HOURS)))
            shopItems.buy(buyItemCommand(WILL_ALSO_TIMEOUT, now().minus(25, HOURS)))
        when:
            paymentTimeoutChecker.checkPaymentTimeouts()
        then:
            readModel.getItemBy(WILL_TIMEOUT).status == "PAYMENT_MISSING"
            readModel.getItemBy(WILL_ALSO_TIMEOUT).status == "PAYMENT_MISSING"
    }

    def 'should not mark payment as missing'() {
        given:
            shopItems.buy(buyItemCommand(WILL_NOT_TIMEOUT, now()))
        when:
            paymentTimeoutChecker.checkPaymentTimeouts()
        then:
            readModel.getItemBy(WILL_NOT_TIMEOUT).status == "BOUGHT"
    }

    def 'should not mark payment as missing when item is paid'() {
        given:
            shopItems.buy(buyItemCommand(ANY_UUID, now()))
        and:
            shopItems.pay(payItemCommand(ANY_UUID, now()))
        when:
            paymentTimeoutChecker.checkPaymentTimeouts()
        then:
            readModel.getItemBy(ANY_UUID).status == "PAID"
    }

}
