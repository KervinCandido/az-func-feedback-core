package br.com.fiap.techchallenge.feedbackplatform.entrypoint.rest;

import br.com.fiap.techchallenge.feedbackplatform.application.dto.CreateFeedbackCommand;
import br.com.fiap.techchallenge.feedbackplatform.application.dto.FeedbackCreatedResult;
import br.com.fiap.techchallenge.feedbackplatform.application.usecase.CreateFeedbackUseCase;
import br.com.fiap.techchallenge.feedbackplatform.entrypoint.rest.dto.CreateAvaliacaoRequest;
import br.com.fiap.techchallenge.feedbackplatform.entrypoint.rest.dto.CreateAvaliacaoResponse;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;

import java.net.URI;

@Path("/avaliacoes")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class AvaliacaoResource {

    private final CreateFeedbackUseCase createFeedbackUseCase;

    @Inject
    public AvaliacaoResource(CreateFeedbackUseCase createFeedbackUseCase) {
        this.createFeedbackUseCase = createFeedbackUseCase;
    }

    @POST
    public Response criar(@Valid CreateAvaliacaoRequest request, @Context UriInfo uriInfo) {
        CreateFeedbackCommand command = new CreateFeedbackCommand(request.descricao(), request.nota());

        FeedbackCreatedResult result = createFeedbackUseCase.execute(command);

        CreateAvaliacaoResponse response = CreateAvaliacaoResponse.from(result);

        URI location = uriInfo.getAbsolutePathBuilder().path(result.id().toString()).build();

        return Response.created(location).entity(response).build();
    }
}
