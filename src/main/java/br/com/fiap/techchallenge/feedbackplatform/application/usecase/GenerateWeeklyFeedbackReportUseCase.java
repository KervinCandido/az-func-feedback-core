package br.com.fiap.techchallenge.feedbackplatform.application.usecase;

import br.com.fiap.techchallenge.feedbackplatform.application.dto.report.FeedbackReportItem;
import br.com.fiap.techchallenge.feedbackplatform.application.dto.report.WeeklyFeedbackReport;
import br.com.fiap.techchallenge.feedbackplatform.application.ports.FeedbackRepositoryPort;
import br.com.fiap.techchallenge.feedbackplatform.domain.enums.Urgencia;
import br.com.fiap.techchallenge.feedbackplatform.domain.model.Feedback;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public class GenerateWeeklyFeedbackReportUseCase {

    private static final ZoneId ZONE_ID_SP = ZoneId.of("America/Sao_Paulo");

    private final FeedbackRepositoryPort feedbackRepository;

    public GenerateWeeklyFeedbackReportUseCase(FeedbackRepositoryPort feedbackRepository) {
        this.feedbackRepository = Objects.requireNonNull(feedbackRepository, "feedbackRepository é obrigatório");
    }

    public WeeklyFeedbackReport execute(OffsetDateTime inicio, OffsetDateTime fim) {
        Objects.requireNonNull(inicio, "inicio é obrigatório");
        Objects.requireNonNull(fim, "fim é obrigatório");

        if (!inicio.isBefore(fim)) {
            throw new IllegalArgumentException("A data inicial deve ser anterior à data final.");
        }

        List<Feedback> feedbacks = feedbackRepository.findByDataCriacaoBetween(inicio, fim);

        List<FeedbackReportItem> itens = feedbacks.stream()
                .map(feedback -> new FeedbackReportItem(
                        feedback.id(),
                        feedback.descricao(),
                        feedback.nota(),
                        feedback.urgencia(),
                        feedback.dataCriacao()
                ))
                .toList();

        double media = feedbacks.stream()
                .mapToInt(Feedback::nota)
                .average()
                .orElse(0.0);

        Map<LocalDate, Long> quantidadePorDia = feedbacks.stream()
                .collect(Collectors.groupingBy(
                        feedback -> feedback.dataCriacao()
                                .atZoneSameInstant(ZONE_ID_SP)
                                .toLocalDate(),
                        Collectors.counting()
                ));

        Map<Urgencia, Long> quantidadePorUrgencia = new EnumMap<>(Urgencia.class);
        for (Urgencia urgencia : Urgencia.values()) {
            quantidadePorUrgencia.put(urgencia, 0L);
        }

        feedbacks.stream()
                .collect(Collectors.groupingBy(
                        Feedback::urgencia,
                        () -> new EnumMap<>(Urgencia.class),
                        Collectors.counting()
                ))
                .forEach(quantidadePorUrgencia::put);

        return new WeeklyFeedbackReport(
                inicio,
                fim,
                feedbacks.size(),
                media,
                quantidadePorDia,
                quantidadePorUrgencia,
                itens
        );
    }
}
