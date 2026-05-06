package br.com.fiap.techchallenge.feedbackplatform.application.dto.report;

import br.com.fiap.techchallenge.feedbackplatform.domain.enums.Urgencia;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;

public record WeeklyFeedbackReport(
        OffsetDateTime inicio,
        OffsetDateTime fim,
        long totalAvaliacoes,
        double mediaAvaliacoes,
        Map<LocalDate, Long> quantidadePorDia,
        Map<Urgencia, Long> quantidadePorUrgencia,
        List<FeedbackReportItem> feedbacks
) {
}
