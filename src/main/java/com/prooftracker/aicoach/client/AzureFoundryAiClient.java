package com.prooftracker.aicoach.client;

import com.openai.azure.credential.AzureApiKeyCredential;
import com.openai.client.OpenAIClient;
import com.openai.client.okhttp.OpenAIOkHttpClient;
import com.openai.models.responses.Response;
import com.openai.models.responses.ResponseCreateParams;
import lombok.Setter;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;


@Service
public class AzureFoundryAiClient {

    private final OpenAIClient client;
    private final String deploymentName;

    public AzureFoundryAiClient(
            @Value("${ai.azure.endpoint}") String endpoint,
            @Value("${ai.azure.api.key}") String apiKey,
            @Value("${ai.azure.deployment}") String deploymentName) {

        this.deploymentName = deploymentName;

        this.client = OpenAIOkHttpClient.builder()
                .baseUrl(endpoint)
                .credential(AzureApiKeyCredential.create(apiKey))
                .build();
    }


    public String generateContent(String prompt) {

        ResponseCreateParams params = ResponseCreateParams.builder()
                .model(deploymentName)
                .input(prompt)
                .build();

        Response response = client.responses().create(params);

        StringBuilder answer = new StringBuilder();

        response.output().stream()
                .flatMap(item -> item.message().stream())
                .flatMap(message -> message.content().stream())
                .flatMap(content -> content.outputText().stream())
                .forEach(text -> answer.append(text.text()));

        return answer.toString();

    }
}
