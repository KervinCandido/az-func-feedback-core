package br.com.fiap.techchallenge.feedbackplatform.application.usecase;

import br.com.fiap.techchallenge.feedbackplatform.application.dto.report.WeeklyFeedbackReport;
import br.com.fiap.techchallenge.feedbackplatform.application.ports.FeedbackRepositoryPort;
import br.com.fiap.techchallenge.feedbackplatform.domain.enums.Urgencia;
import br.com.fiap.techchallenge.feedbackplatform.domain.model.Feedback;
import org.junit.jupiter.api.Test;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class GenerateWeeklyFeedbackReportUseCaseTest {

    @Test
    void deveGerarRelatorioSemanalComResumoDosFeedbacks() {
        OffsetDateTime inicio = OffsetDateTime.of(2026, 5, 1, 0, 0, 0, 0, ZoneOffset.UTC);
        OffsetDateTime fim = OffsetDateTime.of(2026, 5, 8, 0, 0, 0, 0, ZoneOffset.UTC);

        FakeFeedbackRepository repository = new FakeFeedbackRepository(List.of(
                feedback("Aula excelente", 10, Urgencia.BAIXA, OffsetDateTime.of(2026, 5, 1, 10, 0, 0, 0, ZoneOffset.UTC)),
                feedback("Aula razoável", 6, Urgencia.MEDIA, OffsetDateTime.of(2026, 5, 1, 14, 0, 0, 0, ZoneOffset.UTC)),
                feedback("Sistema travando", 2, Urgencia.ALTA, OffsetDateTime.of(2026, 5, 2, 9, 0, 0, 0, ZoneOffset.UTC))
        ));

        GenerateWeeklyFeedbackReportUseCase useCase = new GenerateWeeklyFeedbackReportUseCase(repository);

        WeeklyFeedbackReport report = useCase.execute(inicio, fim);

        assertEquals(inicio, report.inicio());
        assertEquals(fim, report.fim());
        assertEquals(3, report.totalAvaliacoes());
        assertEquals(6.0, report.mediaAvaliacoes());

        assertEquals(2L, report.quantidadePorDia().get(LocalDate.of(2026, 5, 1)));
        assertEquals(1L, report.quantidadePorDia().get(LocalDate.of(2026, 5, 2)));

        assertEquals(1L, report.quantidadePorUrgencia().get(Urgencia.BAIXA));
        assertEquals(1L, report.quantidadePorUrgencia().get(Urgencia.MEDIA));
        assertEquals(1L, report.quantidadePorUrgencia().get(Urgencia.ALTA));

        assertEquals(3, report.feedbacks().size());
        assertEquals("Aula excelente", report.feedbacks().get(0).descricao());
        assertEquals("Sistema travando", report.feedbacks().get(2).descricao());
    }

    @Test
    void deveGerarRelatorioVazioQuandoNaoHouverFeedbacksNoPeriodo() {
        OffsetDateTime inicio = OffsetDateTime.of(2026, 5, 1, 0, 0, 0, 0, ZoneOffset.UTC);
        OffsetDateTime fim = OffsetDateTime.of(2026, 5, 8, 0, 0, 0, 0, ZoneOffset.UTC);

        FakeFeedbackRepository repository = new FakeFeedbackRepository(List.of());

        GenerateWeeklyFeedbackReportUseCase useCase = new GenerateWeeklyFeedbackReportUseCase(repository);

        WeeklyFeedbackReport report = useCase.execute(inicio, fim);

        assertEquals(0, report.totalAvaliacoes());
        assertEquals(0.0, report.mediaAvaliacoes());
        assertTrue(report.quantidadePorDia().isEmpty());

        assertEquals(0L, report.quantidadePorUrgencia().get(Urgencia.BAIXA));
        assertEquals(0L, report.quantidadePorUrgencia().get(Urgencia.MEDIA));
        assertEquals(0L, report.quantidadePorUrgencia().get(Urgencia.ALTA));

        assertTrue(report.feedbacks().isEmpty());
    }

    @Test
    void deveFalharQuandoInicioForNulo() {
        GenerateWeeklyFeedbackReportUseCase useCase = new GenerateWeeklyFeedbackReportUseCase(
                new FakeFeedbackRepository(List.of())
        );

        OffsetDateTime fim = OffsetDateTime.now();

        assertThrows(NullPointerException.class, () -> useCase.execute(null, fim));
    }

    @Test
    void deveFalharQuandoFimForNulo() {
        GenerateWeeklyFeedbackReportUseCase useCase = new GenerateWeeklyFeedbackReportUseCase(
                new FakeFeedbackRepository(List.of())
        );

        OffsetDateTime inicio = OffsetDateTime.now();

        assertThrows(NullPointerException.class, () -> useCase.execute(inicio, null));
    }

    @Test
    void deveFalharQuandoInicioForIgualAoFim() {
        OffsetDateTime data = OffsetDateTime.now();

        GenerateWeeklyFeedbackReportUseCase useCase = new GenerateWeeklyFeedbackReportUseCase(
                new FakeFeedbackRepository(List.of())
        );

        assertThrows(IllegalArgumentException.class, () -> useCase.execute(data, data));
    }

    @Test
    void deveFalharQuandoInicioForDepoisDoFim() {
        OffsetDateTime inicio = OffsetDateTime.of(2026, 5, 8, 0, 0, 0, 0, ZoneOffset.UTC);
        OffsetDateTime fim = OffsetDateTime.of(2026, 5, 1, 0, 0, 0, 0, ZoneOffset.UTC);

        GenerateWeeklyFeedbackReportUseCase useCase = new GenerateWeeklyFeedbackReportUseCase(
                new FakeFeedbackRepository(List.of())
        );

        assertThrows(IllegalArgumentException.class, () -> useCase.execute(inicio, fim));
    }

    private static Feedback feedback(String descricao, int nota, Urgencia urgencia, OffsetDateTime dataCriacao) {
        return new Feedback(
                UUID.randomUUID(),
                descricao,
                nota,
                urgencia,
                dataCriacao
        );
    }

    private static class FakeFeedbackRepository implements FeedbackRepositoryPort {

        private final List<Feedback> feedbacks = new ArrayList<>();

        private FakeFeedbackRepository(List<Feedback> feedbacks) {
            this.feedbacks.addAll(feedbacks);
        }

        @Override
        public Feedback save(Feedback feedback) {
            this.feedbacks.add(feedback);
            return feedback;
        }

        @Override
        public Optional<Feedback> findById(UUID id) {
            return feedbacks.stream()
                    .filter(feedback -> feedback.id().equals(id))
                    .findFirst();
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
