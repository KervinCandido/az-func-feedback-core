package br.com.fiap.techchallenge.feedbackplatform.infrastructure.rest;

import br.com.fiap.techchallenge.feedbackplatform.infrastructure.persistence.repository.PanacheFeedbackRepository;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.OffsetDateTime;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertEquals;

@QuarkusTest
class ReportResourceTest {

    @Inject
    PanacheFeedbackRepository feedbackRepository;

    @BeforeEach
    @Transactional
    void limparBanco() {
        feedbackRepository.deleteAll();
    }

    @Test
    void deveGerarRelatorioSemanalVazioQuandoNaoExistiremFeedbacks() {
        given()
                .queryParam("inicio", "2030-05-01T00:00:00Z")
                .queryParam("fim", "2030-05-08T00:00:00Z")
                .when()
                .get("/reports/weekly")
                .then()
                .statusCode(200)
                .body("totalAvaliacoes", equalTo(0))
                .body("mediaAvaliacoes", equalTo(0.0f))
                .body("quantidadePorUrgencia.BAIXA", equalTo(0))
                .body("quantidadePorUrgencia.MEDIA", equalTo(0))
                .body("quantidadePorUrgencia.ALTA", equalTo(0))
                .body("feedbacks", hasSize(0));
    }

    @Test
    void deveGerarRelatorioSemanalComFeedbacksCriados() {
        OffsetDateTime inicio = OffsetDateTime.now().minusMinutes(5);

        criarAvaliacao("A aula foi muito boa", 9);
        criarAvaliacao("A plataforma está travando durante a aula", 8);

        OffsetDateTime fim = OffsetDateTime.now().plusMinutes(5);

        io.restassured.response.Response response = given()
                .queryParam("inicio", inicio.toString())
                .queryParam("fim", fim.toString())
                .when()
                .get("/reports/weekly")
                .then()
                .statusCode(200)
                .body("totalAvaliacoes", equalTo(2))
                .body("quantidadePorUrgencia.BAIXA", equalTo(1))
                .body("quantidadePorUrgencia.MEDIA", equalTo(0))
                .body("quantidadePorUrgencia.ALTA", equalTo(1))
                .body("feedbacks", hasSize(2))
                .body("feedbacks[0].descricao", equalTo("A aula foi muito boa"))
                .body("feedbacks[1].descricao", equalTo("A plataforma está travando durante a aula"))
                .extract()
                .response();

        float mediaAvaliacoes = response.jsonPath().getFloat("mediaAvaliacoes");

        assertEquals(8.5f, mediaAvaliacoes, 0.001f);
    }

    @Test
    void deveUsarPeriodoPadraoDeSeteDiasQuandoDatasNaoForemInformadas() {
        criarAvaliacao("A aula foi muito boa", 9);

        given()
                .when()
                .get("/reports/weekly")
                .then()
                .statusCode(200)
                .body("totalAvaliacoes", equalTo(1))
                .body("feedbacks", hasSize(1))
                .body("feedbacks[0].descricao", equalTo("A aula foi muito boa"));
    }

    @Test
    void deveRetornarBadRequestQuandoPeriodoForInvalido() {
        given()
                .queryParam("inicio", "2026-05-08T00:00:00Z")
                .queryParam("fim", "2026-05-01T00:00:00Z")
                .when()
                .get("/reports/weekly")
                .then()
                .statusCode(400)
                .body("erro", equalTo("A data inicial deve ser anterior à data final."));
    }

    @Test
    void deveRetornarBadRequestQuandoDataEstiverEmFormatoInvalido() {
        given()
                .queryParam("inicio", "data-invalida")
                .queryParam("fim", "2026-05-01T00:00:00Z")
                .when()
                .get("/reports/weekly")
                .then()
                .statusCode(400)
                .body("erro", equalTo("Parâmetros de data devem estar no formato ISO-8601. Exemplo: 2026-05-01T00:00:00Z."));
    }

    private void criarAvaliacao(String descricao, int nota) {
        given()
                .contentType(ContentType.JSON)
                .body("""
                        {
                          "descricao": "%s",
                          "nota": %d
                        }
                        """.formatted(descricao, nota))
                .when()
                .post("/avaliacoes")
                .then()
                .statusCode(201);
    }
}
