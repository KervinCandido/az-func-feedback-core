package br.com.fiap.techchallenge.feedbackplatform.infrastructure.rest;

import br.com.fiap.techchallenge.feedbackplatform.application.dto.report.StoredWeeklyFeedbackReportResult;
import br.com.fiap.techchallenge.feedbackplatform.application.dto.report.WeeklyFeedbackReport;
import br.com.fiap.techchallenge.feedbackplatform.application.usecase.GenerateAndStoreWeeklyFeedbackReportUseCase;
import br.com.fiap.techchallenge.feedbackplatform.application.usecase.GenerateWeeklyFeedbackReportUseCase;
import io.micrometer.core.annotation.Counted;
import io.micrometer.core.annotation.Timed;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.OffsetDateTime;
import java.time.format.DateTimeParseException;
import java.util.Map;

@Path("/reports")
@Produces(MediaType.APPLICATION_JSON)
public class ReportResource {

    private static final Logger LOG = LoggerFactory.getLogger(ReportResource.class);

    private final GenerateWeeklyFeedbackReportUseCase generateWeeklyFeedbackReportUseCase;
    private final GenerateAndStoreWeeklyFeedbackReportUseCase generateAndStoreWeeklyFeedbackReportUseCase;

    @Inject
    public ReportResource(
            GenerateWeeklyFeedbackReportUseCase generateWeeklyFeedbackReportUseCase,
            GenerateAndStoreWeeklyFeedbackReportUseCase generateAndStoreWeeklyFeedbackReportUseCase) {
        this.generateWeeklyFeedbackReportUseCase = generateWeeklyFeedbackReportUseCase;
        this.generateAndStoreWeeklyFeedbackReportUseCase = generateAndStoreWeeklyFeedbackReportUseCase;
    }

    @GET
    @Path("/weekly")
    @Counted(value = "relatorios.semanais.gerados", description = "Contador de relatórios semanais gerados")
    @Timed(value = "relatorio.semanal.time", description = "Tempo de execução da geração do relatório semanal")
    public Response gerarRelatorioSemanal(
            @QueryParam("inicio") String inicioParam,
            @QueryParam("fim") String fimParam) {

        try {
            OffsetDateTime fim = parseFim(fimParam);
            OffsetDateTime inicio = parseInicio(inicioParam, fim);

            LOG.info("Gerando relatório semanal de {} até {}", inicio, fim);

            WeeklyFeedbackReport report = generateWeeklyFeedbackReportUseCase.execute(inicio, fim);

            return Response.ok(report).build();

        } catch (DateTimeParseException exception) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(Map.of(
                            "erro",
                            "Parâmetros de data devem estar no formato ISO-8601. Exemplo: 2026-05-01T00:00:00Z."))
                    .build();

        } catch (IllegalArgumentException exception) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(Map.of("erro", exception.getMessage()))
                    .build();
        }
    }

    @POST
    @Path("/weekly/storage")
    @Counted(value = "relatorios.semanais.armazenados", description = "Contador de relatórios semanais armazenados")
    @Timed(value = "relatorio.semanal.storage.time", description = "Tempo de geração e armazenamento do relatório semanal")
    public Response gerarEArmazenarRelatorioSemanal(
            @QueryParam("inicio") String inicioParam,
            @QueryParam("fim") String fimParam) {

        try {
            OffsetDateTime fim = parseFim(fimParam);
            OffsetDateTime inicio = parseInicio(inicioParam, fim);

            LOG.info("Gerando e armazenando relatório semanal de {} até {}", inicio, fim);

            StoredWeeklyFeedbackReportResult result =
                    generateAndStoreWeeklyFeedbackReportUseCase.execute(inicio, fim);

            return Response.status(Response.Status.CREATED)
                    .entity(result)
                    .build();

        } catch (DateTimeParseException exception) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(Map.of(
                            "erro",
                            "Parâmetros de data devem estar no formato ISO-8601. Exemplo: 2026-05-01T00:00:00Z."))
                    .build();

        } catch (IllegalArgumentException exception) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(Map.of("erro", exception.getMessage()))
                    .build();
        }
    }

    private OffsetDateTime parseFim(String fimParam) {
        if (fimParam == null || fimParam.isBlank()) {
            return OffsetDateTime.now();
        }

        return OffsetDateTime.parse(fimParam);
    }

    private OffsetDateTime parseInicio(String inicioParam, OffsetDateTime fim) {
        if (inicioParam == null || inicioParam.isBlank()) {
            return fim.minusDays(7);
        }

        return OffsetDateTime.parse(inicioParam);
    }
}
