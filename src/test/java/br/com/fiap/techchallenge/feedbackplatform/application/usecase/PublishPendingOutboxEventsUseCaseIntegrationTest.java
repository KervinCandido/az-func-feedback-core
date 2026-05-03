package br.com.fiap.techchallenge.feedbackplatform.application.usecase;

import br.com.fiap.techchallenge.feedbackplatform.application.dto.PublishOutboxEventsResult;
import br.com.fiap.techchallenge.feedbackplatform.application.ports.OutboxEventRepositoryPort;
import br.com.fiap.techchallenge.feedbackplatform.domain.enums.StatusOutboxEvent;
import br.com.fiap.techchallenge.feedbackplatform.domain.model.OutboxEvent;
import br.com.fiap.techchallenge.feedbackplatform.infrastructure.config.ServiceBusManager;
import br.com.fiap.techchallenge.feedbackplatform.infrastructure.messaging.JacksonEventPayloadSerializerAdapter;
import br.com.fiap.techchallenge.feedbackplatform.infrastructure.messaging.ServiceBusOutboxEventPublisherAdapter;
import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.willDoNothing;

@QuarkusTest
class PublishPendingOutboxEventsUseCaseIntegrationTest {

    private PublishPendingOutboxEventsUseCase publishPendingOutboxEventsUseCase;

    @Inject
    private OutboxEventRepositoryPort outboxEventRepository;

    @Inject
    private JacksonEventPayloadSerializerAdapter serializer;

    @InjectMock
    private ServiceBusManager serviceBusManager;

    @BeforeEach
    void setUp() {
        willDoNothing().given(serviceBusManager).sendMessage(anyString());
        var serviceBusOutboxEventPublisherAdapter = new ServiceBusOutboxEventPublisherAdapter(serviceBusManager, serializer);
        this.publishPendingOutboxEventsUseCase = new PublishPendingOutboxEventsUseCase(outboxEventRepository, serviceBusOutboxEventPublisherAdapter);
    }

    @Test
    @Transactional
    void devePublicarEventoPendenteEAtualizarStatusParaPublicado() {
        OutboxEvent eventoPendente = OutboxEvent.pendente(
                UUID.randomUUID(),
                "feedback.created",
                """
                        {
                          "id": "feedback-id",
                          "descricao": "A plataforma está travando durante a aula",
                          "nota": 8,
                          "urgencia": "ALTA"
                        }
                        """);

        outboxEventRepository.save(eventoPendente);

        PublishOutboxEventsResult result = publishPendingOutboxEventsUseCase.execute(10);

        OutboxEvent eventoAtualizado = outboxEventRepository.findById(eventoPendente.id())
                .orElseThrow();

        assertTrue(result.totalEncontrados() >= 1);
        assertTrue(result.publicados() >= 1);
        assertEquals(0, result.falhas());

        assertEquals(StatusOutboxEvent.PUBLICADO, eventoAtualizado.status());
        assertNotNull(eventoAtualizado.publishedAt());
        assertEquals(0, eventoAtualizado.retryCount());
    }
}
