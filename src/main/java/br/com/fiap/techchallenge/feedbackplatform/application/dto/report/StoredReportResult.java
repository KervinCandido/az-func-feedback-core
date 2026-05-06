package br.com.fiap.techchallenge.feedbackplatform.application.dto.report;

import java.time.OffsetDateTime;

public record StoredReportResult(
        String blobName,
        String blobUrl,
        OffsetDateTime storedAt
) {
}
