package br.com.fiap.techchallenge.feedbackplatform.infrastructure.messaging;

import br.com.fiap.techchallenge.feedbackplatform.application.ports.OutboxEventPublisherPort;
import br.com.fiap.techchallenge.feedbackplatform.domain.model.OutboxEvent;
import br.com.fiap.techchallenge.feedbackplatform.infrastructure.config.ServiceBusManager;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import org.jboss.logging.Logger;

@ApplicationScoped
public class ServiceBusOutboxEventPublisherAdapter implements OutboxEventPublisherPort {

    private static final Logger LOG = Logger.getLogger(ServiceBusOutboxEventPublisherAdapter.class);

    private final ServiceBusManager serviceBusManager;

    private final JacksonEventPayloadSerializerAdapter serializer;

    @Inject
    public ServiceBusOutboxEventPublisherAdapter(ServiceBusManager serviceBusManager,
            JacksonEventPayloadSerializerAdapter serializer) {
        this.serviceBusManager = serviceBusManager;
        this.serializer = serializer;
    }

    @Override
    public void publish(OutboxEvent outboxEvent) {
        LOG.infof(
                "Publicando evento da outbox. eventId=%s, aggregateId=%s, eventType=%s, payload=%s",
                outboxEvent.id(),
                outboxEvent.aggregateId(),
                outboxEvent.eventType(),
                outboxEvent.payload());
        serviceBusManager.sendMessage(serializer.serialize(outboxEvent.payload()));
    }
}
