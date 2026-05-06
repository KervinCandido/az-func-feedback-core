package br.com.fiap.techchallenge.feedbackplatform.infrastructure.report;

import br.com.fiap.techchallenge.feedbackplatform.application.dto.report.WeeklyFeedbackReport;
import br.com.fiap.techchallenge.feedbackplatform.application.ports.WeeklyFeedbackReportSerializerPort;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class JacksonWeeklyFeedbackReportSerializerAdapter implements WeeklyFeedbackReportSerializerPort {

    private final ObjectMapper objectMapper;

    public JacksonWeeklyFeedbackReportSerializerAdapter(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public String serialize(WeeklyFeedbackReport report) {
        try {
            return objectMapper
                    .writerWithDefaultPrettyPrinter()
                    .writeValueAsString(report);
        } catch (JsonProcessingException exception) {
            throw new IllegalStateException("Não foi possível serializar o relatório semanal.", exception);
        }
    }
}
