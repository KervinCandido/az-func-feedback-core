package br.com.fiap.techchallenge.feedbackplatform.domain.model;

import br.com.fiap.techchallenge.feedbackplatform.domain.enums.Urgencia;
import br.com.fiap.techchallenge.feedbackplatform.domain.services.FeedbackUrgenciaClassifier;

import java.time.OffsetDateTime;
import java.util.Objects;
import java.util.UUID;

public record Feedback(
        UUID id,
        String descricao,
        int nota,
        Urgencia urgencia,
        OffsetDateTime dataCriacao) {

    public Feedback {
        Objects.requireNonNull(id, "id é obrigatório");
        validarDescricao(descricao);
        validarNota(nota);
        Objects.requireNonNull(urgencia, "urgencia é obrigatória");
        Objects.requireNonNull(dataCriacao, "dataCriacao é obrigatória");
    }

    public static Feedback novo(String descricao, int nota, FeedbackUrgenciaClassifier classifier) {
        Objects.requireNonNull(classifier, "classifier é obrigatório");

        Urgencia urgenciaCalculada = classifier.classificar(descricao, nota);

        return new Feedback(
                UUID.randomUUID(),
                descricao,
                nota,
                urgenciaCalculada,
                OffsetDateTime.now());
    }

    public Feedback marcarAlertaEnviado(OffsetDateTime dataEnvioAlerta) {
        Objects.requireNonNull(dataEnvioAlerta, "dataEnvioAlerta é obrigatória");

        return new Feedback(
                id,
                descricao,
                nota,
                urgencia,
                dataCriacao);
    }

    private static void validarDescricao(String descricao) {
        if (descricao == null || descricao.isBlank()) {
            throw new IllegalArgumentException("Descrição é obrigatória.");
        }
    }

    private static void validarNota(int nota) {
        if (nota < 0 || nota > 10) {
            throw new IllegalArgumentException("Nota deve estar entre 0 e 10.");
        }
    }
}
