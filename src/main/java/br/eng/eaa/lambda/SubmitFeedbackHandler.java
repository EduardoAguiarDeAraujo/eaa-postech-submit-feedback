package br.eng.eaa.lambda;

import br.eng.eaa.lambda.model.CalculadorUrgencia;
import br.eng.eaa.lambda.model.Feedback;
import br.eng.eaa.lambda.model.FeedbackRequest;
import br.eng.eaa.lambda.model.Urgencia;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import jakarta.inject.Inject;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.services.sns.SnsClient;
import software.amazon.awssdk.services.sns.model.PublishRequest;

import java.time.Instant;
import java.util.UUID;

public class SubmitFeedbackHandler implements RequestHandler<FeedbackRequest, String> {

    @Inject
    DynamoDbEnhancedClient enhancedClient;

    @Inject
    private SnsClient snsClient;

    @Override
    public String handleRequest(FeedbackRequest input, Context context) {

        Feedback feedback = new Feedback();
        feedback.setId(UUID.randomUUID().toString());
        feedback.setDescricao(input.descricao());
        feedback.setNota(input.nota());

        Urgencia urgencia = CalculadorUrgencia.calcular(input.nota());
        feedback.setUrgencia(urgencia.name());
        feedback.setDataEnvio(Instant.now().toString());

        DynamoDbTable<Feedback> table = enhancedClient.table("Feedbacks", TableSchema.fromBean(Feedback.class));
        table.putItem(feedback);

        if (urgencia == Urgencia.ALTA) {
            String mensagem = String.format("Alerta: Feedback crítico recebido! ID: %s - Nota: %d", feedback.getId(), input.nota());

            snsClient.publish(PublishRequest.builder()
                    .topicArn("arn:aws:sns:us-east-1:372365625947:FeedbackAltaUrgenciaTopic")
                    .message(mensagem)
                    .subject("Feedback Crítico Detectado")
                    .build());
        }
        return "Feedback processado com sucesso!";
    }
}
