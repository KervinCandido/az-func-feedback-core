package br.com.fiap.techchallenge.feedbackplatform.application.dto.report;

public record StoredWeeklyFeedbackReportResult(
        WeeklyFeedbackReport report,
        StoredReportResult storage
) {
}
