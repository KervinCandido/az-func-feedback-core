package br.com.fiap.techchallenge.feedbackplatform.application.usecase;

import br.com.fiap.techchallenge.feedbackplatform.application.dto.report.StoredReportResult;
import br.com.fiap.techchallenge.feedbackplatform.application.dto.report.StoredWeeklyFeedbackReportResult;
import br.com.fiap.techchallenge.feedbackplatform.application.dto.report.WeeklyFeedbackReport;
import br.com.fiap.techchallenge.feedbackplatform.application.ports.FeedbackRepositoryPort;
import br.com.fiap.techchallenge.feedbackplatform.application.ports.ReportStoragePort;
import br.com.fiap.techchallenge.feedbackplatform.application.ports.WeeklyFeedbackReportSerializerPort;
import br.com.fiap.techchallenge.feedbackplatform.domain.enums.Urgencia;
import br.com.fiap.techchallenge.feedbackplatform.domain.model.Feedback;
import org.junit.jupiter.api.Test;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class GenerateAndStoreWeeklyFeedbackReportUseCaseTest {

    @Test
    void deveGerarSerializarEArmazenarRelatorioSemanal() {
        OffsetDateTime inicio = OffsetDateTime.of(2026, 5, 1, 0, 0, 0, 0, ZoneOffset.UTC);
        OffsetDateTime fim = OffsetDateTime.of(2026, 5, 8, 0, 0, 0, 0, ZoneOffset.UTC);

        FakeFeedbackRepository repository = new FakeFeedbackRepository(List.of(
                new Feedback(
                        UUID.randomUUID(),
                        "Aula excelente",
                        10,
                        Urgencia.BAIXA,
                        OffsetDateTime.of(2026, 5, 2, 10, 0, 0, 0, ZoneOffset.UTC))
        ));

        GenerateWeeklyFeedbackReportUseCase generateUseCase =
                new GenerateWeeklyFeedbackReportUseCase(repository);

        FakeSerializer serializer = new FakeSerializer();

        FakeStorage storage = new FakeStorage();

        GenerateAndStoreWeeklyFeedbackReportUseCase useCase =
                new GenerateAndStoreWeeklyFeedbackReportUseCase(
                        generateUseCase,
                        serializer,
                        storage
                );

        StoredWeeklyFeedbackReportResult result = useCase.execute(inicio, fim);

        assertNotNull(result);
        assertEquals(1, result.report().totalAvaliacoes());
        assertTrue(result.storage().blobName().startsWith("reports/weekly/relatorio-semanal-feedbacks-"));
        assertTrue(result.storage().blobName().endsWith(".json"));
        assertEquals("application/json", storage.contentTypeSalvo);
        assertEquals("{\"mock\":true}", storage.conteudoSalvo);
        assertEquals(result.storage().blobName(), storage.blobNameSalvo);
    }

    private static class FakeSerializer implements WeeklyFeedbackReportSerializerPort {

        @Override
        public String serialize(WeeklyFeedbackReport report) {
            return "{\"mock\":true}";
        }
    }

    private static class FakeStorage implements ReportStoragePort {

        private String blobNameSalvo;
        private String conteudoSalvo;
        private String contentTypeSalvo;

        @Override
        public StoredReportResult save(String blobName, String content, String contentType) {
            this.blobNameSalvo = blobName;
            this.conteudoSalvo = content;
            this.contentTypeSalvo = contentType;

            return new StoredReportResult(
                    blobName,
                    "http://localhost:10000/devstoreaccount1/feedback-reports/" + blobName,
                    OffsetDateTime.now()
            );
        }
    }

    private static class FakeFeedbackRepository implements FeedbackRepositoryPort {

        private final List<Feedback> feedbacks;

        private FakeFeedbackRepository(List<Feedback> feedbacks) {
            this.feedbacks = feedbacks;
        }

        @Override
        public Feedback save(Feedback feedback) {
            return feedback;
        }

        @Override
        public Optional<Feedback> findById(UUID id) {
            return Optional.empty();
        }

        @Override
        public List<Feedback> findByDataCriacaoBetween(OffsetDateTime inicio, OffsetDateTime fim) {
            return feedbacks.stream()
                    .filter(feedback -> !feedback.dataCriacao().isBefore(inicio))
                    .filter(feedback -> feedback.dataCriacao().isBefore(fim))
                    .toList();
        }
    }
}
