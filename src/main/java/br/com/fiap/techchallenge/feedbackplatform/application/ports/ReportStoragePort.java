package br.com.fiap.techchallenge.feedbackplatform.application.ports;

import br.com.fiap.techchallenge.feedbackplatform.application.dto.report.StoredReportResult;

public interface ReportStoragePort {

    StoredReportResult save(String blobName, String content, String contentType);
}
