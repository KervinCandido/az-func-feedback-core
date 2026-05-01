package br.com.fiap.techchallenge.feedbackplatform.entrypoint.rest;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

@QuarkusTest
class AvaliacaoResourceTest {

    @Test
    void deveCriarAvaliacaoComSucesso() {
        given()
                .contentType(ContentType.JSON)
                .body("""
                        {
                          "descricao": "A plataforma está travando durante a aula",
                          "nota": 8
                        }
                        """)
                .when()
                .post("/avaliacoes")
                .then()
                .statusCode(201)
                .header("Location", notNullValue())
                .body("id", notNullValue())
                .body("descricao", equalTo("A plataforma está travando durante a aula"))
                .body("nota", equalTo(8))
                .body("urgencia", equalTo("ALTA"))
                .body("dataCriacao", notNullValue());
    }

    @Test
    void deveRetornarBadRequestQuandoNotaForMaiorQueDez() {
        given()
                .contentType(ContentType.JSON)
                .body("""
                        {
                          "descricao": "A aula foi boa, mas o áudio estava ruim",
                          "nota": 11
                        }
                        """)
                .when()
                .post("/avaliacoes")
                .then()
                .statusCode(400);
    }

    @Test
    void deveRetornarBadRequestQuandoDescricaoForVazia() {
        given()
                .contentType(ContentType.JSON)
                .body("""
                        {
                          "descricao": "   ",
                          "nota": 5
                        }
                        """)
                .when()
                .post("/avaliacoes")
                .then()
                .statusCode(400);
    }

    @Test
    void deveRetornarBadRequestQuandoNotaNaoForInformada() {
        given()
                .contentType(ContentType.JSON)
                .body("""
                        {
                          "descricao": "A aula foi boa, mas o áudio estava ruim"
                        }
                        """)
                .when()
                .post("/avaliacoes")
                .then()
                .statusCode(400);
    }
}
