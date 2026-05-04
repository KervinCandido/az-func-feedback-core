package br.com.fiap.techchallenge.feedbackplatform.application.usecase;

import br.com.fiap.techchallenge.feedbackplatform.application.dto.CreateFeedbackCommand;
import br.com.fiap.techchallenge.feedbackplatform.application.dto.FeedbackCreatedResult;
import br.com.fiap.techchallenge.feedbackplatform.application.ports.FeedbackRepositoryPort;
import br.com.fiap.techchallenge.feedbackplatform.domain.model.Feedback;
import br.com.fiap.techchallenge.feedbackplatform.domain.services.FeedbackUrgenciaClassifier;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import java.util.Objects;

@ApplicationScoped
public class CreateFeedbackUseCase {

    private final FeedbackRepositoryPort feedbackRepository;
    private final FeedbackUrgenciaClassifier urgenciaClassifier;

    @Inject
    public CreateFeedbackUseCase(FeedbackRepositoryPort feedbackRepository) {
        this(feedbackRepository, new FeedbackUrgenciaClassifier());
    }

    CreateFeedbackUseCase(FeedbackRepositoryPort feedbackRepository, FeedbackUrgenciaClassifier urgenciaClassifier) {
        this.feedbackRepository = Objects.requireNonNull(feedbackRepository);
        this.urgenciaClassifier = Objects.requireNonNull(urgenciaClassifier);
    }

    @Transactional
    public FeedbackCreatedResult execute(CreateFeedbackCommand command) {
        Objects.requireNonNull(command, "command é obrigatório");

        Feedback feedback = Feedback.novo(
                command.descricao(),
                command.nota(),
                urgenciaClassifier);
        Feedback feedbackSalvo = feedbackRepository.save(feedback);
        return FeedbackCreatedResult.from(feedbackSalvo);
    }
}
