package br.com.fiap.techchallenge.feedbackplatform.infrastructure.storage;

import br.com.fiap.techchallenge.feedbackplatform.application.dto.report.StoredReportResult;
import br.com.fiap.techchallenge.feedbackplatform.application.ports.ReportStoragePort;
import com.azure.core.util.BinaryData;
import com.azure.storage.blob.BlobClient;
import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.BlobServiceClient;
import com.azure.storage.blob.BlobServiceClientBuilder;
import com.azure.storage.blob.models.BlobHttpHeaders;
import jakarta.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import java.time.OffsetDateTime;

@ApplicationScoped
public class AzureBlobReportStorageAdapter implements ReportStoragePort {

    private final String connectionString;
    private final String containerName;

    public AzureBlobReportStorageAdapter(
            @ConfigProperty(name = "app.reports.storage.connection-string") String connectionString,
            @ConfigProperty(name = "app.reports.storage.container-name") String containerName) {
        this.connectionString = connectionString;
        this.containerName = containerName;
    }

    @Override
    public StoredReportResult save(String blobName, String content, String contentType) {
        BlobServiceClient serviceClient = new BlobServiceClientBuilder()
                .connectionString(connectionString)
                .buildClient();

        BlobContainerClient containerClient = serviceClient.getBlobContainerClient(containerName);
        containerClient.createIfNotExists();

        BlobClient blobClient = containerClient.getBlobClient(blobName);

        blobClient.upload(BinaryData.fromString(content), true);
        blobClient.setHttpHeaders(new BlobHttpHeaders().setContentType(contentType));

        return new StoredReportResult(
                blobName,
                blobClient.getBlobUrl(),
                OffsetDateTime.now()
        );
    }
}
