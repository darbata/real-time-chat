package io.darbata.chat;

import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import java.util.concurrent.atomic.AtomicInteger;

@Component
public class WebSocketMetrics {

    private final AtomicInteger sessions = new AtomicInteger(0);

    public WebSocketMetrics(MeterRegistry registry) {
        Gauge.builder("sessions", sessions, AtomicInteger::get)
                .description("Number of websocket sessions on this instance")
                .register(registry);
    }

    @EventListener
    public void onConnected(SessionConnectedEvent event) {
        sessions.incrementAndGet();
    }

    @EventListener
    public void onDisconnect(SessionDisconnectEvent event) {
        sessions.decrementAndGet();
    }
}
