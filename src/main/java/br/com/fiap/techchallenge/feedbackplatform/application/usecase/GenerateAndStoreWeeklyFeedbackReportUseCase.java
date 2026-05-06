package br.com.fiap.techchallenge.feedbackplatform.application.usecase;

import br.com.fiap.techchallenge.feedbackplatform.application.dto.report.StoredReportResult;
import br.com.fiap.techchallenge.feedbackplatform.application.dto.report.StoredWeeklyFeedbackReportResult;
import br.com.fiap.techchallenge.feedbackplatform.application.dto.report.WeeklyFeedbackReport;
import br.com.fiap.techchallenge.feedbackplatform.application.ports.ReportStoragePort;
import br.com.fiap.techchallenge.feedbackplatform.application.ports.WeeklyFeedbackReportSerializerPort;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

public class GenerateAndStoreWeeklyFeedbackReportUseCase {

    private static final DateTimeFormatter FILE_DATE_FORMATTER =
            DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss").withZone(ZoneOffset.UTC);

    private final GenerateWeeklyFeedbackReportUseCase generateWeeklyFeedbackReportUseCase;
    private final WeeklyFeedbackReportSerializerPort serializer;
    private final ReportStoragePort storage;

    public GenerateAndStoreWeeklyFeedbackReportUseCase(
            GenerateWeeklyFeedbackReportUseCase generateWeeklyFeedbackReportUseCase,
            WeeklyFeedbackReportSerializerPort serializer,
            ReportStoragePort storage) {
        this.generateWeeklyFeedbackReportUseCase = Objects.requireNonNull(
                generateWeeklyFeedbackReportUseCase,
                "generateWeeklyFeedbackReportUseCase é obrigatório");
        this.serializer = Objects.requireNonNull(serializer, "serializer é obrigatório");
        this.storage = Objects.requireNonNull(storage, "storage é obrigatório");
    }

    public StoredWeeklyFeedbackReportResult execute(OffsetDateTime inicio, OffsetDateTime fim) {
        WeeklyFeedbackReport report = generateWeeklyFeedbackReportUseCase.execute(inicio, fim);

        String content = serializer.serialize(report);
        String blobName = buildBlobName(inicio, fim);

        StoredReportResult storedReport = storage.save(
                blobName,
                content,
                "application/json"
        );

        return new StoredWeeklyFeedbackReportResult(report, storedReport);
    }

    private String buildBlobName(OffsetDateTime inicio, OffsetDateTime fim) {
        String inicioFormatado = FILE_DATE_FORMATTER.format(inicio.toInstant());
        String fimFormatado = FILE_DATE_FORMATTER.format(fim.toInstant());

        return "reports/weekly/relatorio-semanal-feedbacks-%s-%s.json"
                .formatted(inicioFormatado, fimFormatado);
    }
}
