package habr.broker;


import org.springframework.amqp.rabbit.connection.CorrelationData;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;

// Вспомогательный класс-обёртка
class ConfirmLatchWrapper extends CorrelationData {
    private final CountDownLatch latch;
    private final AtomicBoolean ack = new AtomicBoolean(false);
    private String cause;

    public ConfirmLatchWrapper(CountDownLatch latch) {
        this.latch = latch;
    }

    public CountDownLatch getLatch() {
        return latch;
    }

    public boolean isAck() {
        return ack.get();
    }

    public void setAck(boolean ack) {
        this.ack.set(ack);
    }

    public String getCause() {
        return cause;
    }

    public void setCause(String cause) {
        this.cause = cause;
    }
}

