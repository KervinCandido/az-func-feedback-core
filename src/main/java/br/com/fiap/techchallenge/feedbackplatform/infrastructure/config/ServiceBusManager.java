package br.com.fiap.techchallenge.feedbackplatform.infrastructure.config;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.azure.messaging.servicebus.ServiceBusClientBuilder;
import com.azure.messaging.servicebus.ServiceBusMessage;
import com.azure.messaging.servicebus.ServiceBusSenderClient;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class ServiceBusManager {

    Logger LOG = LoggerFactory.getLogger(ServiceBusManager.class);

    @ConfigProperty(name = "quarkus.azure.servicebus.queue-name", defaultValue = "sbq-feedbacks-critical-queue")
    private String queueName;

    @Inject
    private ServiceBusClientBuilder clientBuilder;

    private ServiceBusSenderClient senderClient;

    @PostConstruct
    void initialize() {
        LOG.info("Initializing Azure Service Bus clients");

        // Initialize sender client
        senderClient = clientBuilder
                .sender()
                .queueName(queueName)
                .buildClient();

        LOG.info("Azure Service Bus clients initialized successfully");
    }

    @PreDestroy
    void cleanup() {
        LOG.info("Cleaning up Azure Service Bus clients");

        if (senderClient != null) {
            senderClient.close();
        }

        LOG.info("Azure Service Bus clients cleaned up successfully");
    }

    public void sendMessage(String messageBody) {
        LOG.info("Sending message: {}", messageBody);

        ServiceBusMessage message = new ServiceBusMessage(messageBody);
        senderClient.sendMessage(message);

        LOG.info("Message sent successfully");
    }
}
