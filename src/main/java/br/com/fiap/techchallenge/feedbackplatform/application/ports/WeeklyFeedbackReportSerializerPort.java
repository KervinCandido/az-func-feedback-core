package br.com.fiap.techchallenge.feedbackplatform.application.ports;

import br.com.fiap.techchallenge.feedbackplatform.application.dto.report.WeeklyFeedbackReport;

public interface WeeklyFeedbackReportSerializerPort {

    String serialize(WeeklyFeedbackReport report);
}
